package com.aceshot.musicplayer.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aceshot.musicplayer.data.model.Song
import com.aceshot.musicplayer.presentation.screens.library.LibraryScreen
import com.aceshot.musicplayer.presentation.screens.nowplaying.NowPlayingScreen
import com.aceshot.musicplayer.presentation.screens.playlists.PlaylistDetailScreen
import com.aceshot.musicplayer.presentation.screens.playlists.PlaylistsScreen
import com.aceshot.musicplayer.presentation.screens.search.SearchScreen
import com.aceshot.musicplayer.presentation.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    onPlaySongs: (List<Song>, Int) -> Unit,
    onAddToPlaylist: (Song) -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Library.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Screen.Library.route) {
            LibraryScreen(onPlaySongs = onPlaySongs, onAddToPlaylist = onAddToPlaylist)
        }
        composable(Screen.Search.route) {
            SearchScreen(onPlaySongs = onPlaySongs)
        }
        composable(Screen.Playlists.route) {
            PlaylistsScreen(
                onNavigateToDetail = { playlistId ->
                    navController.navigate(Screen.PlaylistDetail.createRoute(playlistId))
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(Screen.NowPlaying.route) {
            NowPlayingScreen()
        }
        composable(
            route = Screen.PlaylistDetail.route,
            arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
            PlaylistDetailScreen(
                playlistId = playlistId,
                onBack = { navController.popBackStack() },
                onPlaySongs = onPlaySongs
            )
        }
    }
}
