# Glass Music — Android musiqa pleyeri

Glassmorphism uslubidagi musiqa pleyeri. Kotlin + Jetpack Compose + Media3 (ExoPlayer) asosida.
Telefondagi MP3 / FLAC / M4A / OGG fayllarni topib ijro etadi.

## Imkoniyatlar

- **Bosh sahifa** — vaqtga qarab salomlashuv, "Kun miksi" (har kuni yangilanadi), yangi qo'shilgan qo'shiqlar karuseli, "Siz uchun" ro'yxati
- **Qidiruv** — nom, ijrochi yoki albom bo'yicha tezkor qidiruv
- **Kutubxona** — barcha qo'shiqlar va Sevimlilar, aralashtirib yoki ketma-ket ijro
- **Pleyer** — xiralashtirilgan muqova foni, waveform progress (bosib yoki surib o'tkazish), aralashtirish, takrorlash (hammasi / bittasi), navbat oynasi
- **Mini-pleyer** va shisha pastki navigatsiya
- **Fonda ijro** — bildirishnoma pleyeri, qulf ekrani, quloqchin tugmalari; quloqchin uzilsa avtomatik pauza
- Sevimlilar telefon xotirasida saqlanadi

## Talablar

- Android Studio (Ladybug 2024.2 yoki yangiroq)
- JDK 17 (Android Studio ichida bor)
- Android 8.0 (API 26) va undan yuqori telefon

## Ishga tushirish

1. Zip faylni oching va `GlassMusic` papkasini Android Studio'da **File → Open** orqali oching.
2. Gradle sinxronizatsiyasi tugashini kuting (birinchi marta kutubxonalar yuklanadi, internet kerak).
3. Telefonni USB orqali ulang (Sozlamalar → Dasturchi uchun → USB debugging yoqilgan bo'lsin) yoki emulyator tanlang.
4. **Run ▶** tugmasini bosing.
5. Ilova ochilganda "Musiqa va audio" ruxsatini bering.

> Emulyatorda sinash uchun: MP3 faylni emulyator oynasiga sudrab tashlang, u `Download` papkasiga tushadi.

APK yig'ish: **Build → Build App Bundle(s) / APK(s) → Build APK(s)**.
Tayyor fayl: `app/build/outputs/apk/debug/app-debug.apk`.

## Tuzilishi

```
app/src/main/java/uz/glassmusic/
├── MainActivity.kt            — kirish nuqtasi, edge-to-edge
├── MusicViewModel.kt          — holat: kutubxona, pleyer, sevimlilar
├── data/
│   ├── Song.kt                — qo'shiq modeli, vaqt formatlash
│   └── MusicRepository.kt     — MediaStore'dan qo'shiqlarni o'qish
├── playback/
│   └── PlaybackService.kt     — Media3 fon xizmati (bildirishnoma, qulf ekrani)
└── ui/
    ├── GlassMusicApp.kt       — ruxsat, navigatsiya, mini-pleyer
    ├── theme/Theme.kt         — ranglar va shriftlar
    ├── components/
    │   ├── Glass.kt           — shisha sirt, fon, muqova, waveform, nav bar
    │   └── GlassIcons.kt      — ikonkalar
    └── screens/               — Home, Search, Library, Player, Permission
```

## Moslashtirish

- **Aksent rangi:** `ui/theme/Theme.kt` → `GlassColors.Accent`
  (dizayndagi muqobillar: `0xFF7CF0D4` yalpiz, `0xFFA78BFF` binafsha, `0xFFFFC46B` sariq)
- **Shisha qalinligi:** `Modifier.glass(..., strength = 0.10f)` — kattaroq qiymat sutliroq shisha
- **Fon dog'lari:** `GlassBackdrop()` ichidagi `orb(...)` chaqiruvlari
- **Shrift:** hozir tizim shrifti. Plus Jakarta Sans qo'shish uchun `.ttf` fayllarni `res/font/` ga qo'yib,
  `Theme.kt` dagi `Typography` ga `fontFamily` bering.

## Eslatma

Pleyer foni Android 12+ da haqiqiy blur bilan chiziladi; eski versiyalarda o'rniga qorong'iroq qatlam ishlatiladi.
Pastki panel va mini-pleyer yarim shaffof shisha — orqadagi ro'yxat ular orqali xira ko'rinadi.
Panellar ostida ham haqiqiy "backdrop blur" kerak bo'lsa, keyingi qadamda
[Haze](https://github.com/chrisbanes/haze) kutubxonasini qo'shish mumkin.
