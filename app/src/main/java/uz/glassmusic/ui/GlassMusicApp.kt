package uz.glassmusic.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import uz.glassmusic.MusicViewModel
import uz.glassmusic.ui.components.BottomScrim
import uz.glassmusic.ui.components.GlassBackdrop
import uz.glassmusic.ui.components.GlassIcons
import uz.glassmusic.ui.components.GlassNavBar
import uz.glassmusic.ui.components.MiniPlayer
import uz.glassmusic.ui.components.NavItem
import uz.glassmusic.ui.screens.HomeScreen
import uz.glassmusic.ui.screens.LibraryScreen
import uz.glassmusic.ui.screens.PermissionScreen
import uz.glassmusic.ui.screens.PlayerScreen
import uz.glassmusic.ui.screens.SearchScreen

private object Routes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val LIBRARY = "library"
    const val PLAYER = "player"
}

private val audioPermission =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO
    else Manifest.permission.READ_EXTERNAL_STORAGE

private val navItems = listOf(
    NavItem(Routes.HOME, "Bosh sahifa", GlassIcons.HomeOutline, GlassIcons.Home),
    NavItem(Routes.SEARCH, "Qidiruv", GlassIcons.Search, GlassIcons.Search),
    NavItem(Routes.LIBRARY, "Kutubxona", GlassIcons.Library, GlassIcons.Library),
)

@Composable
fun GlassMusicApp(vm: MusicViewModel = viewModel()) {
    val context = LocalContext.current
    val activity = context as Activity

    fun hasPermission() =
        ContextCompat.checkSelfPermission(context, audioPermission) == PackageManager.PERMISSION_GRANTED

    var granted by remember { mutableStateOf(hasPermission()) }
    var deniedForever by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        granted = result[audioPermission] == true || hasPermission()
        deniedForever = !granted &&
            !ActivityCompat.shouldShowRequestPermissionRationale(activity, audioPermission)
    }

    fun requestPermissions() {
        val perms = buildList {
            add(audioPermission)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        launcher.launch(perms.toTypedArray())
    }

    // Sozlamalardan qaytganda ruxsatni qayta tekshiramiz
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        val now = hasPermission()
        if (now != granted) granted = now
    }

    LaunchedEffect(granted) {
        if (granted) vm.loadSongs()
    }

    Box(Modifier.fillMaxSize()) {
        GlassBackdrop()

        if (!granted) {
            PermissionScreen(
                deniedForever = deniedForever,
                onRequest = { requestPermissions() },
                onOpenSettings = {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                    )
                },
            )
        } else {
            MainContent(vm)
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.MainContent(vm: MusicViewModel) {
        val library by vm.library.collectAsStateWithLifecycle()
        val player by vm.player.collectAsStateWithLifecycle()
        val favorites by vm.favorites.collectAsStateWithLifecycle()

        val nav = rememberNavController()
        val backStack by nav.currentBackStackEntryAsState()
        val route = backStack?.destination?.route
        val currentId = player.current?.id

        NavHost(
            navController = nav,
            startDestination = Routes.HOME,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    library = library,
                    currentSongId = currentId,
                    onOpenSearch = { nav.navigateTab(Routes.SEARCH) },
                    onOpenLibrary = { nav.navigateTab(Routes.LIBRARY) },
                    onPlay = { list, i -> vm.playList(list, i) },
                )
            }
            composable(Routes.SEARCH) {
                SearchScreen(
                    songs = library.songs,
                    currentSongId = currentId,
                    onPlay = { list, i -> vm.playList(list, i) },
                )
            }
            composable(Routes.LIBRARY) {
                LibraryScreen(
                    library = library,
                    favorites = favorites,
                    currentSongId = currentId,
                    onPlay = { list, i, shuffle -> vm.playList(list, i, shuffle) },
                    onToggleFavorite = vm::toggleFavorite,
                )
            }
            composable(
                Routes.PLAYER,
                enterTransition = { slideInVertically(initialOffsetY = { it }) + fadeIn() },
                popExitTransition = { slideOutVertically(targetOffsetY = { it }) + fadeOut() },
            ) {
                PlayerScreen(
                    state = player,
                    isFavorite = currentId != null && currentId in favorites,
                    onClose = { nav.popBackStack() },
                    onTogglePlay = vm::togglePlay,
                    onNext = vm::next,
                    onPrevious = vm::previous,
                    onSeek = vm::seekToFraction,
                    onToggleShuffle = vm::toggleShuffle,
                    onCycleRepeat = vm::cycleRepeat,
                    onToggleFavorite = { player.current?.let(vm::toggleFavorite) },
                    onPlayFromQueue = vm::playQueueIndex,
                )
            }
        }

        val showChrome = route != Routes.PLAYER
        if (showChrome) BottomScrim()

        AnimatedVisibility(
            visible = showChrome,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        ) {
            Column(
                Modifier
                    .navigationBarsPadding()
                    .padding(start = 14.dp, end = 14.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val song = player.current
                if (song != null) {
                    MiniPlayer(
                        song = song,
                        isPlaying = player.isPlaying,
                        progress = player.progress,
                        isFavorite = song.id in favorites,
                        onOpen = { nav.navigate(Routes.PLAYER) { launchSingleTop = true } },
                        onTogglePlay = vm::togglePlay,
                        onToggleFavorite = { vm.toggleFavorite(song) },
                    )
                }
                GlassNavBar(
                    items = navItems,
                    selectedRoute = route,
                    onSelect = { nav.navigateTab(it.route) },
                )
            }
        }
}

private fun androidx.navigation.NavController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
