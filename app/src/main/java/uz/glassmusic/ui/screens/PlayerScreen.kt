package uz.glassmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.glassmusic.PlayerUiState
import uz.glassmusic.RepeatMode
import uz.glassmusic.data.Song
import uz.glassmusic.data.formatDuration
import uz.glassmusic.ui.components.AlbumArt
import uz.glassmusic.ui.components.BlurredArtBackground
import uz.glassmusic.ui.components.GlassIconButton
import uz.glassmusic.ui.components.GlassIcons
import uz.glassmusic.ui.components.SongRow
import uz.glassmusic.ui.components.WaveformSeekBar
import uz.glassmusic.ui.components.glass
import uz.glassmusic.ui.theme.GlassColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    state: PlayerUiState,
    isFavorite: Boolean,
    onClose: () -> Unit,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Float) -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleFavorite: () -> Unit,
    onPlayFromQueue: (Int) -> Unit,
) {
    val song = state.current
    var previewFraction by remember { mutableStateOf<Float?>(null) }
    var showQueue by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        BlurredArtBackground(song)

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 22.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            // Yuqori qator
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                GlassIconButton(GlassIcons.ChevronDown, "Yopish", onClose)
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("HOZIR IJRODA", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.78f))
                    Text(
                        song?.album?.ifBlank { null } ?: "Glass Music",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
                GlassIconButton(GlassIcons.Queue, "Navbat", { showQueue = true })
            }

            if (song == null) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Hech narsa ijro etilmayapti", color = GlassColors.TextSecondary)
                }
            } else {
                NowPlayingBody(
                    song = song,
                    state = state,
                    isFavorite = isFavorite,
                    previewFraction = previewFraction,
                    onPreview = { previewFraction = it },
                    onTogglePlay = onTogglePlay,
                    onNext = onNext,
                    onPrevious = onPrevious,
                    onSeek = onSeek,
                    onToggleShuffle = onToggleShuffle,
                    onCycleRepeat = onCycleRepeat,
                    onToggleFavorite = onToggleFavorite,
                    onShowQueue = { showQueue = true },
                )
            }
        }
    }

    if (showQueue) {
        ModalBottomSheet(
            onDismissRequest = { showQueue = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = Color(0xFF16141F).copy(alpha = 0.96f),
            contentColor = Color.White,
        ) {
            Text(
                "Navbat · ${state.queue.size} ta qo'shiq",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn(contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 32.dp)) {
                itemsIndexed(state.queue, key = { i, s -> "$i-${s.id}" }) { i, s ->
                    SongRow(
                        song = s,
                        subtitle = "${s.artist} · ${formatDuration(s.durationMs)}",
                        isCurrent = s.id == song?.id,
                        onClick = { onPlayFromQueue(i) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.NowPlayingBody(
    song: Song,
    state: PlayerUiState,
    isFavorite: Boolean,
    previewFraction: Float?,
    onPreview: (Float?) -> Unit,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Float) -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShowQueue: () -> Unit,
) {
            // Muqova
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                AlbumArt(
                    song,
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        .shadow(30.dp, RoundedCornerShape(36.dp)),
                    corner = 36.dp,
                )
            }

            // Nomi + sevimli
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(song.title, fontSize = 26.sp, style = MaterialTheme.typography.headlineMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(song.artist, fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                GlassIconButton(
                    icon = if (isFavorite) GlassIcons.Heart else GlassIcons.HeartOutline,
                    contentDescription = if (isFavorite) "Sevimlilardan olib tashlash" else "Sevimlilarga qo'shish",
                    onClick = onToggleFavorite,
                    size = 52.dp,
                    tint = if (isFavorite) GlassColors.Accent else Color.White,
                )
            }

            // Boshqaruv paneli (shisha)
            Column(
                Modifier
                    .fillMaxWidth()
                    .glass(RoundedCornerShape(34.dp), strength = 0.11f)
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    WaveformSeekBar(
                        seed = song.id,
                        progress = state.progress,
                        onSeek = onSeek,
                        onPreview = onPreview,
                    )
                    val shownPos = previewFraction?.let { (it * state.durationMs).toLong() } ?: state.positionMs
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(formatDuration(shownPos), style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                        Text(formatDuration(state.durationMs), style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                    }
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onToggleShuffle, modifier = Modifier.size(48.dp)) {
                        Icon(
                            GlassIcons.Shuffle,
                            contentDescription = if (state.shuffle) "Aralashtirishni o'chirish" else "Aralashtirish",
                            tint = if (state.shuffle) GlassColors.Accent else Color.White.copy(alpha = 0.85f),
                        )
                    }
                    IconButton(onClick = onPrevious, modifier = Modifier.size(56.dp)) {
                        Icon(GlassIcons.Previous, "Oldingi", tint = Color.White, modifier = Modifier.size(30.dp))
                    }
                    Box(
                        Modifier
                            .size(80.dp)
                            .shadow(16.dp, RoundedCornerShape(28.dp))
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White)
                            .clickable(onClick = onTogglePlay),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            if (state.isPlaying) GlassIcons.Pause else GlassIcons.Play,
                            contentDescription = if (state.isPlaying) "Pauza" else "Ijro etish",
                            tint = GlassColors.Background,
                            modifier = Modifier.size(30.dp),
                        )
                    }
                    IconButton(onClick = onNext, modifier = Modifier.size(56.dp)) {
                        Icon(GlassIcons.Next, "Keyingi", tint = Color.White, modifier = Modifier.size(30.dp))
                    }
                    IconButton(onClick = onCycleRepeat, modifier = Modifier.size(48.dp)) {
                        Icon(
                            if (state.repeat == RepeatMode.ONE) GlassIcons.RepeatOne else GlassIcons.Repeat,
                            contentDescription = when (state.repeat) {
                                RepeatMode.OFF -> "Takrorlash o'chiq"
                                RepeatMode.ALL -> "Hammasini takrorlash"
                                RepeatMode.ONE -> "Bittasini takrorlash"
                            },
                            tint = if (state.repeat != RepeatMode.OFF) GlassColors.Accent else Color.White.copy(alpha = 0.85f),
                        )
                    }
                }
            }

            // Keyingi qo'shiq
            val idx = state.queue.indexOfFirst { it.id == song.id }
            val upNext = state.queue.getOrNull(idx + 1)
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .glass(RoundedCornerShape(26.dp))
                    .clickable(onClick = onShowQueue)
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(GlassIcons.Queue, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Text(
                    if (upNext != null) "Keyingi: ${upNext.title}" else "Navbat",
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
}
