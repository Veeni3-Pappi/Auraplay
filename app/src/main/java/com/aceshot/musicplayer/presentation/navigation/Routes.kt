package com.aceshot.musicplayer.presentation.navigation

sealed class Screen(val route: String) {
    object Library : Screen("library")
    object Search : Screen("search")
    object Playlists : Screen("playlists")
    object Settings : Screen("settings")
    object NowPlaying : Screen("now_playing")
    object PlaylistDetail : Screen("playlist_detail/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlist_detail/$playlistId"
    }
}
