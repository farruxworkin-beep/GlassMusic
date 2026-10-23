package uz.glassmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.glassmusic.LibraryUiState
import uz.glassmusic.data.Song
import uz.glassmusic.data.formatDuration
import uz.glassmusic.data.formatTotal
import uz.glassmusic.ui.components.AlbumArt
import uz.glassmusic.ui.components.GlassIcons
import uz.glassmusic.ui.components.SongRow
import uz.glassmusic.ui.components.glass
import uz.glassmusic.ui.theme.GlassColors
import java.time.LocalTime

private fun greeting(): String = when (LocalTime.now().hour) {
    in 5..11 -> "Xayrli tong"
    in 12..17 -> "Xayrli kun"
    in 18..22 -> "Xayrli kech"
    else -> "Xayrli tun"
}

@Composable
fun HomeScreen(
    library: LibraryUiState,
    currentSongId: Long?,
    onOpenSearch: () -> Unit,
    onOpenLibrary: () -> Unit,
    onPlay: (List<Song>, Int) -> Unit,
) {
    if (library.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = GlassColors.Accent)
        }
        return
    }
    if (library.songs.isEmpty()) {
        EmptyLibrary()
        return
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 200.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        item {
            Column {
                Text(greeting(), fontSize = 14.sp, color = GlassColors.TextSecondary, fontWeight = FontWeight.Medium)
                Text("Bugun nima tinglaymiz?", style = MaterialTheme.typography.headlineLarge)
            }
        }

        // Qidiruv maydoni — bosilganda qidiruv ekrani ochiladi
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .glass(RoundedCornerShape(28.dp))
                    .clickable(onClick = onOpenSearch)
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(GlassIcons.Search, contentDescription = null, tint = Color.White)
                Text("Qo'shiq, ijrochi yoki albom", fontSize = 16.sp, color = GlassColors.TextSecondary)
            }
        }

        // Kun miksi
        item {
            val mix = library.dailyMix
            Row(
                Modifier
                    .fillMaxWidth()
                    .glass(RoundedCornerShape(28.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                MixCover(mix, Modifier.size(116.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("KUN MIKSI", style = MaterialTheme.typography.labelSmall, color = GlassColors.TextSecondary)
                    Text("Bugungi tanlov", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "${mix.size} ta qo'shiq · ${formatTotal(mix.sumOf { it.durationMs })}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GlassColors.TextSecondary,
                    )
                    Row(
                        Modifier
                            .padding(top = 4.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .clickable { onPlay(mix, 0) }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(GlassIcons.Play, null, tint = GlassColors.Background, modifier = Modifier.size(14.dp))
                        Text("Tinglash", color = GlassColors.Background, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        // Yangi qo'shilganlar — gorizontal karusel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader("Yangi qo'shilganlar", null, null)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    itemsIndexed(library.recentlyAdded, key = { _, s -> s.id }) { i, song ->
                        Column(
                            Modifier
                                .width(140.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onPlay(library.recentlyAdded, i) },
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            AlbumArt(song, Modifier.size(140.dp), corner = 20.dp)
                            Column {
                                Text(
                                    song.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    song.artist,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlassColors.TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
        }

        item { SectionHeader("Siz uchun", "Barchasi", onOpenLibrary) }

        val picks = library.dailyMix.take(6)
        itemsIndexed(picks, key = { _, s -> "pick-${s.id}" }) { i, song ->
            SongRow(
                song = song,
                subtitle = "${song.artist} · ${formatDuration(song.durationMs)}",
                isCurrent = song.id == currentSongId,
                onClick = { onPlay(picks, i) },
                modifier = Modifier.padding(top = 0.dp),
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, action: String?, onAction: (() -> Unit)?) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        if (action != null && onAction != null) {
            Text(
                action,
                color = Color(0xFFFFB3DC),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onAction)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
            )
        }
    }
}

/** 4 ta muqovadan yig'ilgan miks rasmi. */
@Composable
fun MixCover(songs: List<Song>, modifier: Modifier = Modifier) {
    val four = songs.take(4)
    Box(modifier.clip(RoundedCornerShape(20.dp))) {
        if (four.size < 4) {
            AlbumArt(four.firstOrNull(), Modifier.fillMaxSize(), corner = 0.dp)
        } else {
            Column(Modifier.fillMaxSize()) {
                Row(Modifier.weight(1f)) {
                    AlbumArt(four[0], Modifier.weight(1f).fillMaxSize(), corner = 0.dp)
                    AlbumArt(four[1], Modifier.weight(1f).fillMaxSize(), corner = 0.dp)
                }
                Row(Modifier.weight(1f)) {
                    AlbumArt(four[2], Modifier.weight(1f).fillMaxSize(), corner = 0.dp)
                    AlbumArt(four[3], Modifier.weight(1f).fillMaxSize(), corner = 0.dp)
                }
            }
        }
    }
}

@Composable
fun EmptyLibrary() {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .fillMaxWidth()
                .glass(RoundedCornerShape(28.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(GlassIcons.Note, null, tint = GlassColors.Accent, modifier = Modifier.size(44.dp))
            Text("Musiqa topilmadi", style = MaterialTheme.typography.titleLarge)
            Text(
                "Telefoningizga MP3, FLAC yoki M4A fayllarni yuklang — ular shu yerda avtomatik paydo bo'ladi.",
                style = MaterialTheme.typography.bodySmall,
                color = GlassColors.TextSecondary,
            )
            Spacer(Modifier.height(4.dp))
        }
    }
}
