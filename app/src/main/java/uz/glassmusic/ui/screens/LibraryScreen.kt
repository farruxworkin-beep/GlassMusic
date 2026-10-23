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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.glassmusic.LibraryUiState
import uz.glassmusic.data.Song
import uz.glassmusic.data.formatDuration
import uz.glassmusic.data.formatTotal
import uz.glassmusic.ui.components.GlassIconButton
import uz.glassmusic.ui.components.GlassIcons
import uz.glassmusic.ui.components.SongRow
import uz.glassmusic.ui.components.glass
import uz.glassmusic.ui.theme.GlassColors

@Composable
fun LibraryScreen(
    library: LibraryUiState,
    favorites: Set<Long>,
    currentSongId: Long?,
    onPlay: (List<Song>, Int, Boolean) -> Unit,
    onToggleFavorite: (Song) -> Unit,
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val list = if (tab == 0) library.songs else library.songs.filter { it.id in favorites }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 200.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MixCover(list, Modifier.size(132.dp).shadow(24.dp, RoundedCornerShape(20.dp)))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("KUTUBXONA", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                    Text(
                        if (tab == 0) "Barcha qo'shiqlar" else "Sevimlilar",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        "${list.size} ta qo'shiq · ${formatTotal(list.sumOf { it.durationMs })}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GlassColors.TextSecondary,
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
        }

        // Segment tanlagich + tugmalar
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    Modifier
                        .weight(1f)
                        .height(48.dp)
                        .glass(RoundedCornerShape(24.dp))
                        .padding(3.dp),
                ) {
                    listOf("Hammasi", "Sevimlilar").forEachIndexed { i, label ->
                        val selected = tab == i
                        Box(
                            Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .then(
                                    if (selected) Modifier.glass(RoundedCornerShape(21.dp), strength = 0.16f)
                                    else Modifier.clip(RoundedCornerShape(21.dp))
                                )
                                .clickable { tab = i },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                label,
                                fontSize = 14.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (selected) Color.White else Color.White.copy(alpha = 0.8f),
                            )
                        }
                    }
                }
                GlassIconButton(GlassIcons.Shuffle, "Aralashtirib ijro etish", {
                    if (list.isNotEmpty()) onPlay(list, (list.indices).random(), true)
                }, size = 52.dp)
                Box(
                    Modifier
                        .size(60.dp)
                        .shadow(12.dp, RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(22.dp))
                        .background(GlassColors.Accent)
                        .clickable { if (list.isNotEmpty()) onPlay(list, 0, false) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(GlassIcons.Play, "Hammasini ijro etish", tint = GlassColors.OnAccent, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        if (list.isEmpty()) {
            item {
                Text(
                    if (tab == 1) "Hali sevimli qo'shiq yo'q. Yurakcha belgisini bosib qo'shing."
                    else "Qo'shiqlar topilmadi.",
                    color = GlassColors.TextSecondary,
                    modifier = Modifier.padding(vertical = 24.dp),
                )
            }
        }

        itemsIndexed(list, key = { _, s -> s.id }) { i, song ->
            val fav = song.id in favorites
            SongRow(
                song = song,
                number = i + 1,
                subtitle = "${song.artist} · ${formatDuration(song.durationMs)}",
                isCurrent = song.id == currentSongId,
                onClick = { onPlay(list, i, false) },
                trailing = {
                    Box(
                        Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .clickable { onToggleFavorite(song) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            if (fav) GlassIcons.Heart else GlassIcons.HeartOutline,
                            contentDescription = if (fav) "Sevimlilardan olib tashlash" else "Sevimlilarga qo'shish",
                            tint = if (fav) GlassColors.Accent else Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
            )
        }
    }
}
