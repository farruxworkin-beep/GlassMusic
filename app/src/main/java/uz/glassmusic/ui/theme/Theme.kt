package uz.glassmusic.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Dizayndagi ranglar. Aksentni shu yerda almashtirsangiz, butun ilova o'zgaradi. */
object GlassColors {
    val Background = Color(0xFF0A0912)
    val Accent = Color(0xFFFF7AC6)        // pushti (muqobil: 0xFF7CF0D4, 0xFFA78BFF, 0xFFFFC46B)
    val OnAccent = Color(0xFF1A0614)
    val Violet = Color(0xFF6C5CFF)
    val Teal = Color(0xFF2FD3C3)
    val TextPrimary = Color.White
    val TextSecondary = Color.White.copy(alpha = 0.72f)
    val TextTertiary = Color.White.copy(alpha = 0.55f)
}

private val AppTypography = Typography(
    headlineLarge = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.4).sp),
    titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold),
    titleMedium = TextStyle(fontSize = 19.sp, fontWeight = FontWeight.Bold),
    bodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold),
    bodySmall = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
    labelMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
    labelSmall = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp),
)

@Composable
fun GlassMusicTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = GlassColors.Accent,
            onPrimary = GlassColors.OnAccent,
            background = GlassColors.Background,
            surface = Color(0xFF16141F),
            onBackground = GlassColors.TextPrimary,
            onSurface = GlassColors.TextPrimary,
        ),
        typography = AppTypography,
        content = content,
    )
}
