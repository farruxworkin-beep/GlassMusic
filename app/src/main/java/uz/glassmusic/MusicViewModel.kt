package uz.glassmusic

import android.app.Application
import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uz.glassmusic.data.MusicRepository
import uz.glassmusic.data.Song
import uz.glassmusic.playback.PlaybackService
import java.time.LocalDate

enum class RepeatMode { OFF, ALL, ONE }

data class PlayerUiState(
    val current: Song? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val shuffle: Boolean = false,
    val repeat: RepeatMode = RepeatMode.OFF,
    val queue: List<Song> = emptyList(),
) {
    val progress: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
}

data class LibraryUiState(
    val loading: Boolean = true,
    val songs: List<Song> = emptyList(),
    val dailyMix: List<Song> = emptyList(),
    val recentlyAdded: List<Song> = emptyList(),
)

class MusicViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = MusicRepository(app)
    private val prefs = app.getSharedPreferences("glass_music", Context.MODE_PRIVATE)

    private val _library = MutableStateFlow(LibraryUiState())
    val library: StateFlow<LibraryUiState> = _library.asStateFlow()

    private val _player = MutableStateFlow(PlayerUiState())
    val player: StateFlow<PlayerUiState> = _player.asStateFlow()

    private val _favorites = MutableStateFlow(
        prefs.getStringSet(KEY_FAVORITES, emptySet())!!.mapNotNull { it.toLongOrNull() }.toSet()
    )
    val favorites: StateFlow<Set<Long>> = _favorites.asStateFlow()

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null
    private var songsById: Map<String, Song> = emptyMap()

    private val listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) = syncFromPlayer()
    }

    init {
        connectController()
        startProgressLoop()
    }

    // ---------- Kutubxona ----------

    fun loadSongs() {
        viewModelScope.launch {
            _library.update { it.copy(loading = true) }
            val songs = repository.loadSongs()
            songsById = songs.associateBy { it.id.toString() }
            val seed = LocalDate.now().toEpochDay()
            _library.value = LibraryUiState(
                loading = false,
                songs = songs,
                dailyMix = songs.shuffled(kotlin.random.Random(seed)).take(25),
                recentlyAdded = songs.sortedByDescending { it.dateAddedSec }.take(8),
            )
            syncFromPlayer()
        }
    }

    fun toggleFavorite(song: Song) {
        _favorites.update { set -> if (song.id in set) set - song.id else set + song.id }
        prefs.edit().putStringSet(KEY_FAVORITES, _favorites.value.map { it.toString() }.toSet()).apply()
    }

    // ---------- Ijro ----------

    /** Ro'yxatni navbatga qo'yib, [startIndex] dagi qo'shiqdan boshlaydi. */
    fun playList(songs: List<Song>, startIndex: Int = 0, shuffle: Boolean = false) {
        val c = controller ?: return
        if (songs.isEmpty()) return
        c.shuffleModeEnabled = shuffle
        c.setMediaItems(songs.map { it.toMediaItem() }, startIndex.coerceIn(songs.indices), 0L)
        c.prepare()
        c.play()
    }

    /** Navbatdagi [index] chi qo'shiqqa o'tadi. */
    fun playQueueIndex(index: Int) {
        val c = controller ?: return
        if (index in 0 until c.mediaItemCount) {
            c.seekToDefaultPosition(index)
            c.play()
        }
    }

    fun togglePlay() {
        val c = controller ?: return
        if (c.isPlaying) c.pause() else {
            if (c.playbackState == Player.STATE_IDLE) c.prepare()
            c.play()
        }
    }

    fun next() { controller?.seekToNext() }

    fun previous() { controller?.seekToPrevious() }

    fun seekToFraction(fraction: Float) {
        val c = controller ?: return
        val dur = c.duration
        if (dur > 0) {
            c.seekTo((dur * fraction.coerceIn(0f, 1f)).toLong())
            syncFromPlayer()
        }
    }

    fun seekBy(deltaMs: Long) {
        val c = controller ?: return
        c.seekTo((c.currentPosition + deltaMs).coerceAtLeast(0L))
    }

    fun toggleShuffle() {
        controller?.let { it.shuffleModeEnabled = !it.shuffleModeEnabled }
    }

    fun cycleRepeat() {
        controller?.let {
            it.repeatMode = when (it.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                else -> Player.REPEAT_MODE_OFF
            }
        }
    }

    // ---------- Ichki qism ----------

    private fun connectController() {
        val ctx = getApplication<Application>()
        val token = SessionToken(ctx, ComponentName(ctx, PlaybackService::class.java))
        val future = MediaController.Builder(ctx, token).buildAsync()
        controllerFuture = future
        future.addListener({
            runCatching { future.get() }.onSuccess { c ->
                controller = c
                c.addListener(listener)
                syncFromPlayer()
            }
        }, ContextCompat.getMainExecutor(ctx))
    }

    private fun startProgressLoop() {
        viewModelScope.launch {
            while (isActive) {
                val c = controller
                if (c != null && c.isPlaying) {
                    _player.update {
                        it.copy(
                            positionMs = c.currentPosition,
                            durationMs = c.duration.coerceAtLeast(0L)
                        )
                    }
                }
                delay(500)
            }
        }
    }

    private fun syncFromPlayer() {
        val c = controller ?: return
        val queue = (0 until c.mediaItemCount).mapNotNull { songsById[c.getMediaItemAt(it).mediaId] }
        _player.value = PlayerUiState(
            current = c.currentMediaItem?.let { songsById[it.mediaId] },
            isPlaying = c.isPlaying,
            positionMs = c.currentPosition,
            durationMs = c.duration.coerceAtLeast(0L),
            shuffle = c.shuffleModeEnabled,
            repeat = when (c.repeatMode) {
                Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                else -> RepeatMode.OFF
            },
            queue = queue,
        )
    }

    private fun Song.toMediaItem(): MediaItem =
        MediaItem.Builder()
            .setMediaId(id.toString())
            .setUri(uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setArtworkUri(artUri)
                    .build()
            )
            .build()

    override fun onCleared() {
        controller?.removeListener(listener)
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controller = null
        super.onCleared()
    }

    private companion object {
        const val KEY_FAVORITES = "favorites"
    }
}
