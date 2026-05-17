package com.aceshot.musicplayer.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aceshot.musicplayer.presentation.screens.library.LibraryScreen
import com.aceshot.musicplayer.presentation.screens.playlists.PlaylistsScreen
import com.aceshot.musicplayer.presentation.screens.search.SearchScreen
import com.aceshot.musicplayer.presentation.screens.settings.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Screen.Library.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Screen.Library.route) {
            LibraryScreen()
        }
        composable(Screen.Search.route) {
            SearchScreen()
        }
        composable(Screen.Playlists.route) {
            PlaylistsScreen(onNavigateToDetail = { /* Navigate to detail */ })
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
