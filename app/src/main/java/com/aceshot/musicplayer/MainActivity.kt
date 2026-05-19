package com.aceshot.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aceshot.musicplayer.data.model.Song
import com.aceshot.musicplayer.presentation.components.AddToPlaylistDialog
import com.aceshot.musicplayer.presentation.components.MiniPlayer
import com.aceshot.musicplayer.presentation.navigation.BottomNavBar
import com.aceshot.musicplayer.presentation.navigation.NavGraph
import com.aceshot.musicplayer.presentation.navigation.Screen
import com.aceshot.musicplayer.presentation.screens.onboarding.OnboardingScreen
import com.aceshot.musicplayer.presentation.viewmodel.NowPlayingViewModel
import com.aceshot.musicplayer.presentation.viewmodel.OnboardingViewModel
import com.aceshot.musicplayer.presentation.viewmodel.PlaylistViewModel
import com.aceshot.musicplayer.ui.theme.AuraplayTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuraplayTheme {
                val onboardingViewModel: OnboardingViewModel = hiltViewModel()
                val hasCompleted by onboardingViewModel.hasCompletedOnboarding.collectAsStateWithLifecycle()

                if (!hasCompleted) {
                    OnboardingScreen(
                        onComplete = { /* State will auto-update via Flow */ },
                        viewModel = onboardingViewModel
                    )
                } else {
                    val navController = rememberNavController()
                    val nowPlayingViewModel: NowPlayingViewModel = hiltViewModel()
                    val playlistViewModel: PlaylistViewModel = hiltViewModel()
                    val currentSong by nowPlayingViewModel.currentSong.collectAsStateWithLifecycle()
                    val isPlaying by nowPlayingViewModel.isPlaying.collectAsStateWithLifecycle()
                    val progress by nowPlayingViewModel.progress.collectAsStateWithLifecycle()
                    val playlists by playlistViewModel.playlists.collectAsStateWithLifecycle()

                    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }

                    if (songToAddToPlaylist != null) {
                        AddToPlaylistDialog(
                            playlists = playlists,
                            onDismiss = { songToAddToPlaylist = null },
                            onPlaylistSelected = { playlistId ->
                                songToAddToPlaylist?.let { song ->
                                    playlistViewModel.addSongToPlaylist(playlistId, song.id)
                                }
                                songToAddToPlaylist = null
                            },
                            onCreateNew = { name ->
                                songToAddToPlaylist?.let { song ->
                                    playlistViewModel.createPlaylistAndAddSong(name, song.id)
                                }
                                songToAddToPlaylist = null
                            }
                        )
                    }

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val showMiniPlayer = currentRoute != Screen.NowPlaying.route

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            Column {
                                if (showMiniPlayer) {
                                    MiniPlayer(
                                        song = currentSong,
                                        isPlaying = isPlaying,
                                        progress = progress,
                                        onPlayPauseClick = { nowPlayingViewModel.togglePlayPause() },
                                        onClick = {
                                            navController.navigate(Screen.NowPlaying.route) {
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                }
                                BottomNavBar(navController)
                            }
                        }
                    ) { innerPadding ->
                        NavGraph(
                            navController = navController,
                            innerPadding = innerPadding,
                            onPlaySongs = { songs, index ->
                                nowPlayingViewModel.playSong(songs, index)
                            },
                            onAddToPlaylist = { song ->
                                songToAddToPlaylist = song
                            }
                        )
                    }
                }
            }
        }
    }
}
