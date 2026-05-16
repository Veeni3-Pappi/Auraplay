package com.aceshot.musicplayer.presentation.navigation

sealed class Screen(val route: String) {
    object Library : Screen("library")
    object Search : Screen("search")
    object Playlists : Screen("playlists")
    object Settings : Screen("settings")
}
