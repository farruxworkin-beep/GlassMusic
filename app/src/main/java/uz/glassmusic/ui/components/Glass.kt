package uz.glassmusic.ui.components

import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import uz.glassmusic.data.Song
import uz.glassmusic.ui.theme.GlassColors
import kotlin.math.abs
import kotlin.random.Random

// ------------------------------------------------------------------
// Shisha (glass) sirt
// ------------------------------------------------------------------

/**
 * Glassmorphism sirti: yarim shaffof oq qatlam, yuqoridan yorug'lik tushgandek
 * gradient va ingichka yorqin chegara.
 * [strength] — shishaning qanchalik "sutli" ekanligi (0.06–0.2).
 * [tint] — orqadagi kontent ko'p bo'lsa o'qilishi uchun qorong'i asos (pastki panel, mini-pleyer).
 */
fun Modifier.glass(
    shape: Shape,
    strength: Float = 0.10f,
    tint: Color = Color.Transparent,
): Modifier = this
    .clip(shape)
    .background(tint, shape)
    .background(
        Brush.verticalGradient(
            listOf(Color.White.copy(alpha = strength + 0.07f), Color.White.copy(alpha = strength))
        ),
        shape,
    )
    .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.40f), Color.White.copy(alpha = 0.08f))
        ),
        shape = shape,
    )

/** Butun ilova orqasidagi rangli "nur dog'lari" — shisha effektini ko'rsatadigan fon. */
@Composable
fun GlassBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxSize()) {
        drawRect(GlassColors.Background)
        fun orb(color: Color, center: Offset, radius: Float, alpha: Float) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(color.copy(alpha = alpha), color.copy(alpha = alpha * 0.35f), Color.Transparent),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
                center = center,
            )
        }
        val w = size.width
        val h = size.height
        orb(GlassColors.Accent, Offset(w * 0.05f, h * 0.06f), w * 0.75f, 0.55f)
        orb(GlassColors.Violet, Offset(w * 1.0f, h * 0.42f), w * 0.70f, 0.50f)
        orb(GlassColors.Teal, Offset(w * 0.15f, h * 0.85f), w * 0.65f, 0.30f)
    }
}

/** Kichik dumaloq shisha tugma (orqaga, yurakcha va h.k.). */
@Composable
fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tint: Color = Color.White,
) {
    Box(
        modifier
            .size(size)
            .glass(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription, tint = tint, modifier = Modifier.size(22.dp))
    }
}

// ------------------------------------------------------------------
// Albom muqovasi
// ------------------------------------------------------------------

private val fallbackGradients = listOf(
    listOf(Color(0xFFFF4FA3), Color(0xFFFFB36B)),
    listOf(Color(0xFF6CF2E0), Color(0xFF2D3BFF)),
    listOf(Color(0xFF8B5CFF), Color(0xFFFF6FB5)),
    listOf(Color(0xFFFFE066), Color(0xFFFF7A45)),
    listOf(Color(0xFF3FE0D0), Color(0xFF1B1530)),
    listOf(Color(0xFF0F2A4A), Color(0xFFFF8A5B)),
)

fun fallbackBrush(seed: Long): Brush =
    Brush.linearGradient(fallbackGradients[(abs(seed) % fallbackGradients.size).toInt()])

/** Muqova: fayl ichidagi rasm, bo'lmasa chiroyli gradient va nota belgisi. */
@Composable
fun AlbumArt(
    song: Song?,
    modifier: Modifier = Modifier,
    corner: Dp = 14.dp,
) {
    val shape = RoundedCornerShape(corner)
    Box(
        modifier
            .clip(shape)
            .background(fallbackBrush(song?.albumId ?: 0L), shape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            GlassIcons.Note,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.55f),
            modifier = Modifier.fillMaxSize(0.38f),
        )
        if (song != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(song.artUri)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/** Pleyer ekrani orqasidagi xiralashtirilgan muqova. Android 12+ da haqiqiy blur. */
@Composable
fun BlurredArtBackground(song: Song?, modifier: Modifier = Modifier) {
    val canBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    Box(modifier.fillMaxSize().background(GlassColors.Background)) {
        Box(
            Modifier
                .fillMaxSize()
                .then(if (canBlur) Modifier.blur(80.dp) else Modifier)
                .background(fallbackBrush(song?.albumId ?: 0L))
        ) {
            if (song != null) {
                AsyncImage(
                    model = song.artUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        // Blur bo'lmagan qurilmalarda kuchliroq qoraytiramiz
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0912).copy(alpha = if (canBlur) 0.40f else 0.72f))
        )
    }
}

// ------------------------------------------------------------------
// Qo'shiq qatori
// ------------------------------------------------------------------

@Composable
fun SongRow(
    song: Song,
    subtitle: String,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    number: Int? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val accent = GlassColors.Accent
    val rowShape = RoundedCornerShape(20.dp)
    Row(
        modifier
            .fillMaxWidth()
            .then(if (isCurrent) Modifier.glass(rowShape, strength = 0.10f) else Modifier.clip(rowShape))
            .clickable(onClick = onClick)
            .padding(start = if (number != null) 10.dp else 6.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (number != null) {
            Text(
                "$number",
                modifier = Modifier.width(22.dp),
                color = if (isCurrent) accent else GlassColors.TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
        AlbumArt(song, Modifier.size(52.dp))
        Column(Modifier.weight(1f)) {
            Text(
                song.title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCurrent) accent else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = GlassColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        trailing?.invoke()
    }
}

// ------------------------------------------------------------------
// To'lqin (waveform) ko'rinishidagi progress — bosib yoki surib o'tkazish mumkin
// ------------------------------------------------------------------

@Composable
fun WaveformSeekBar(
    seed: Long,
    progress: Float,
    onSeek: (Float) -> Unit,
    onPreview: (Float?) -> Unit,
    modifier: Modifier = Modifier,
    bars: Int = 44,
) {
    val heights = remember(seed) {
        val r = Random(seed)
        List(bars) { 0.25f + r.nextFloat() * 0.75f }
    }
    var dragFraction by remember { mutableStateOf<Float?>(null) }
    val seek by rememberUpdatedState(onSeek)
    val preview by rememberUpdatedState(onPreview)
    val shown = dragFraction ?: progress
    val accent = GlassColors.Accent

    Canvas(
        modifier
            .fillMaxWidth()
            .height(44.dp)
            .pointerInput(Unit) {
                detectTapGestures { o -> seek((o.x / size.width).coerceIn(0f, 1f)) }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { o ->
                        dragFraction = (o.x / size.width).coerceIn(0f, 1f)
                        preview(dragFraction)
                    },
                    onDragEnd = {
                        dragFraction?.let { seek(it) }
                        dragFraction = null
                        preview(null)
                    },
                    onDragCancel = {
                        dragFraction = null
                        preview(null)
                    },
                    onHorizontalDrag = { change, _ ->
                        change.consume()
                        dragFraction = (change.position.x / size.width).coerceIn(0f, 1f)
                        preview(dragFraction)
                    },
                )
            }
    ) {
        val gap = 3.dp.toPx()
        val barW = (size.width - gap * (bars - 1)) / bars
        heights.forEachIndexed { i, hf ->
            val barH = size.height * hf
            val x = i * (barW + gap)
            val played = (i + 0.5f) / bars <= shown
            drawRoundRect(
                color = if (played) accent else Color.White.copy(alpha = 0.32f),
                topLeft = Offset(x, (size.height - barH) / 2f),
                size = Size(barW, barH),
                cornerRadius = CornerRadius(barW / 2f, barW / 2f),
            )
        }
    }
}

// ------------------------------------------------------------------
// Pastki qism: mini-pleyer va navigatsiya
// ------------------------------------------------------------------

private val BarTint = Color(0xFF14111F).copy(alpha = 0.78f)

@Composable
fun MiniPlayer(
    song: Song,
    isPlaying: Boolean,
    progress: Float,
    isFavorite: Boolean,
    onOpen: () -> Unit,
    onTogglePlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier
            .fillMaxWidth()
            .height(68.dp)
            .glass(shape, strength = 0.09f, tint = BarTint)
            .clickable(onClick = onOpen)
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AlbumArt(song, Modifier.size(48.dp))
            Column(Modifier.weight(1f)) {
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
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    if (isFavorite) GlassIcons.Heart else GlassIcons.HeartOutline,
                    contentDescription = if (isFavorite) "Sevimlilardan olib tashlash" else "Sevimlilarga qo'shish",
                    tint = if (isFavorite) GlassColors.Accent else Color.White,
                )
            }
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onTogglePlay),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (isPlaying) GlassIcons.Pause else GlassIcons.Play,
                    contentDescription = if (isPlaying) "Pauza" else "Ijro etish",
                    tint = GlassColors.Background,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        // Ingichka progress chizig'i
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 22.dp)
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.18f))
        ) {
            Box(
                Modifier
                    .fillMaxWidth(progress)
                    .height(3.dp)
                    .background(GlassColors.Accent)
            )
        }
    }
}

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
)

/** Material 3 uslubidagi pastki navigatsiya, shisha ko'rinishida. */
@Composable
fun GlassNavBar(
    items: List<NavItem>,
    selectedRoute: String?,
    onSelect: (NavItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(76.dp)
            .glass(RoundedCornerShape(28.dp), strength = 0.09f, tint = BarTint)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            val selected = item.route == selectedRoute
            Column(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelect(item) }
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    Modifier
                        .width(64.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) GlassColors.Accent else Color.Transparent),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (selected) item.selectedIcon else item.icon,
                        contentDescription = null,
                        tint = if (selected) GlassColors.OnAccent else Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(22.dp),
                    )
                }
                Text(
                    item.label,
                    fontSize = 12.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (selected) Color.White else Color.White.copy(alpha = 0.85f),
                )
            }
        }
    }
}

/** Ekran pastidagi qorong'ilashuv — ro'yxat panellar ostida ham o'qiladigan bo'ladi. */
@Composable
fun BoxScope.BottomScrim(height: Dp = 220.dp) {
    Box(
        Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .height(height)
            .background(
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.65f to GlassColors.Background.copy(alpha = 0.9f),
                    1f to GlassColors.Background.copy(alpha = 0.95f),
                )
            )
    )
}
