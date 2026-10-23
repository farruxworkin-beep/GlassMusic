package uz.glassmusic.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** MediaStore orqali telefondagi barcha musiqalarni o'qiydi. */
class MusicRepository(private val context: Context) {

    private val albumArtBase: Uri = Uri.parse("content://media/external/audio/albumart")

    suspend fun loadSongs(): List<Song> = withContext(Dispatchers.IO) {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
        )
        // Faqat musiqa, 30 soniyadan qisqa ovozlar (qo'ng'iroq, xabar) chiqarilmaydi
        val selection =
            "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= ?"
        val args = arrayOf("30000")
        val sort = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"

        val result = mutableListOf<Song>()
        context.contentResolver.query(collection, projection, selection, args, sort)?.use { c ->
            val idCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dateCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            while (c.moveToNext()) {
                val id = c.getLong(idCol)
                val albumId = c.getLong(albumIdCol)
                val artist = c.getString(artistCol)
                result += Song(
                    id = id,
                    title = c.getString(titleCol) ?: "Nomsiz",
                    artist = if (artist.isNullOrBlank() || artist == "<unknown>") "Noma'lum ijrochi" else artist,
                    album = c.getString(albumCol) ?: "",
                    albumId = albumId,
                    durationMs = c.getLong(durCol),
                    uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id),
                    artUri = ContentUris.withAppendedId(albumArtBase, albumId),
                    dateAddedSec = c.getLong(dateCol),
                )
            }
        }
        result
    }
}
