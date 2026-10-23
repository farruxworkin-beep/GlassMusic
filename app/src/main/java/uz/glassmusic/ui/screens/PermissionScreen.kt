package uz.glassmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uz.glassmusic.ui.components.GlassIcons
import uz.glassmusic.ui.components.glass
import uz.glassmusic.ui.theme.GlassColors

/**
 * Musiqa fayllarini o'qish ruxsatini so'raydigan ekran.
 * [deniedForever] bo'lsa, foydalanuvchini sozlamalarga yo'naltiramiz.
 */
@Composable
fun PermissionScreen(
    deniedForever: Boolean,
    onRequest: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .fillMaxWidth()
                .glass(RoundedCornerShape(32.dp), strength = 0.11f)
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                Modifier
                    .size(72.dp)
                    .glass(RoundedCornerShape(24.dp), strength = 0.14f),
                contentAlignment = Alignment.Center,
            ) {
                Icon(GlassIcons.Note, null, tint = GlassColors.Accent, modifier = Modifier.size(36.dp))
            }
            Text("Glass Music", style = MaterialTheme.typography.headlineMedium)
            Text(
                if (deniedForever)
                    "Ruxsat o'chirilgan. Sozlamalar → Ruxsatlar → Musiqa va audio bo'limida ruxsat bering."
                else
                    "Telefoningizdagi qo'shiqlarni ko'rsatish uchun musiqa va audio fayllariga ruxsat kerak. Fayllar hech qayerga yuborilmaydi.",
                style = MaterialTheme.typography.bodySmall,
                color = GlassColors.TextSecondary,
                textAlign = TextAlign.Center,
            )
            Box(
                Modifier
                    .padding(top = 6.dp)
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(GlassColors.Accent)
                    .clickable(onClick = if (deniedForever) onOpenSettings else onRequest),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (deniedForever) "Sozlamalarni ochish" else "Ruxsat berish",
                    color = GlassColors.OnAccent,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
