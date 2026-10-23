package uz.glassmusic.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Dizayndagi ikonkalar (24x24). Icon() ularni tint rangi bilan bo'yaydi,
 * shuning uchun bu yerdagi qora rang ahamiyatsiz.
 */
object GlassIcons {
    val Home = filled("home", "M12 3l9 7.5V20a1 1 0 0 1-1 1h-5v-6h-6v6H4a1 1 0 0 1-1-1v-9.5z")
    val HomeOutline = stroked("home_o", "M12 3l9 7.5V20a1 1 0 0 1-1 1h-5v-6h-6v6H4a1 1 0 0 1-1-1v-9.5z")
    val Search = stroked(
        "search", "M4.5 10.5a6 6 0 1 0 12 0a6 6 0 1 0 -12 0", "M15 15l5 5", width = 2.1f
    )
    val Library = stroked(
        "library", "M4 6h10M4 11h10M4 16h6",
        "M14.5 17a2.5 2.5 0 1 0 5 0a2.5 2.5 0 1 0 -5 0", "M19.5 17V7l2 1"
    )
    val Play = filled("play", "M7 4l13 8-13 8z")
    val Pause = filled("pause", "M6 4h4v16H6zM14 4h4v16h-4z")
    val Next = filled("next", "M16 5h2v14h-2zM4 5v14l11-7z")
    val Previous = filled("prev", "M6 5h2v14H6zM20 5v14L9 12z")
    val Shuffle = stroked("shuffle", "M16 4h4v4M4 20L20 4M16 20h4v-4M14 14l6 6M4 4l5 5", width = 2f)
    val Repeat = stroked(
        "repeat", "M17 2l3 3-3 3", "M4 11V9a4 4 0 0 1 4-4h12",
        "M7 22l-3-3 3-3", "M20 13v2a4 4 0 0 1-4 4H4", width = 2f
    )
    val RepeatOne = stroked(
        "repeat_one", "M17 2l3 3-3 3", "M4 11V9a4 4 0 0 1 4-4h12",
        "M7 22l-3-3 3-3", "M20 13v2a4 4 0 0 1-4 4H4", "M11 10l1.5-1v6", width = 2f
    )
    val Heart = filled("heart", "M12 20s-7-4.4-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.6-7 10-7 10z")
    val HeartOutline = stroked("heart_o", "M12 20s-7-4.4-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.6-7 10-7 10z")
    val ChevronDown = stroked("chev_down", "M6 9l6 6 6-6", width = 2f)
    val Back = stroked("back", "M19 12H5M11 6l-6 6 6 6", width = 2f)
    val Queue = stroked("queue", "M4 6h10M4 11h10M4 16h6", "M17 9v10M13 15l4 4 4-4")
    val Close = stroked("close", "M6 6l12 12M18 6L6 18", width = 2f)
    val Note = stroked(
        "note", "M9 18V5l11-2v13",
        "M3 18a3 3 0 1 0 6 0a3 3 0 1 0 -6 0", "M14 16a3 3 0 1 0 6 0a3 3 0 1 0 -6 0"
    )

    private fun filled(name: String, vararg paths: String) = build(name, true, 0f, paths)

    private fun stroked(name: String, vararg paths: String, width: Float = 1.8f) =
        build(name, false, width, paths)

    private fun build(name: String, fill: Boolean, width: Float, paths: Array<out String>): ImageVector {
        val builder = ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        )
        paths.forEach { d ->
            builder.addPath(
                pathData = addPathNodes(d),
                fill = if (fill) SolidColor(Color.Black) else null,
                stroke = if (fill) null else SolidColor(Color.Black),
                strokeLineWidth = width,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
        return builder.build()
    }
}
