package uz.glassmusic

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import uz.glassmusic.ui.GlassMusicApp
import uz.glassmusic.ui.theme.GlassMusicTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ekran chetigacha chizish; status va navigatsiya panellari shaffof, ikonkalar oq
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            GlassMusicTheme {
                GlassMusicApp()
            }
        }
    }
}
