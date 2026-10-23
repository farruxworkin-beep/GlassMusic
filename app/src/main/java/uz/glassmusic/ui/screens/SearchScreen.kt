package uz.glassmusic.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.glassmusic.data.Song
import uz.glassmusic.data.formatDuration
import uz.glassmusic.ui.components.GlassIcons
import uz.glassmusic.ui.components.SongRow
import uz.glassmusic.ui.components.glass
import uz.glassmusic.ui.theme.GlassColors

@Composable
fun SearchScreen(
    songs: List<Song>,
    currentSongId: Long?,
    onPlay: (List<Song>, Int) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }

    val q = query.trim()
    val results = remember(q, songs) {
        if (q.isEmpty()) emptyList()
        else songs.filter {
            it.title.contains(q, ignoreCase = true) ||
                it.artist.contains(q, ignoreCase = true) ||
                it.album.contains(q, ignoreCase = true)
        }
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 200.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            Text("Qidiruv", style = MaterialTheme.typography.headlineLarge)
        }
        item {
            Row(
                Modifier
                    .padding(top = 14.dp, bottom = 12.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .glass(RoundedCornerShape(28.dp))
                    .padding(start = 18.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(GlassIcons.Search, contentDescription = null, tint = Color.White)
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (query.isEmpty()) {
                        Text("Qo'shiq, ijrochi yoki albom", fontSize = 16.sp, color = GlassColors.TextSecondary)
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                        cursorBrush = SolidColor(GlassColors.Accent),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focus),
                    )
                }
                if (query.isNotEmpty()) {
                    Box(
                        Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable { query = "" },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(GlassIcons.Close, "Tozalash", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        if (q.isNotEmpty() && results.isEmpty()) {
            item {
                Text(
                    "\"$q\" bo'yicha hech narsa topilmadi",
                    color = GlassColors.TextSecondary,
                    modifier = Modifier.padding(vertical = 24.dp),
                )
            }
        }

        itemsIndexed(results, key = { _, s -> s.id }) { i, song ->
            SongRow(
                song = song,
                subtitle = "${song.artist} · ${formatDuration(song.durationMs)}",
                isCurrent = song.id == currentSongId,
                onClick = { onPlay(results, i) },
            )
        }
    }
}
