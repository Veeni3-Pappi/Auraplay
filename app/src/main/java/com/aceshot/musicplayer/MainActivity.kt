package com.aceshot.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.aceshot.musicplayer.presentation.components.MiniPlayer
import com.aceshot.musicplayer.presentation.navigation.BottomNavBar
import com.aceshot.musicplayer.presentation.navigation.NavGraph
import com.aceshot.musicplayer.presentation.navigation.Screen
import com.aceshot.musicplayer.presentation.screens.onboarding.OnboardingScreen
import com.aceshot.musicplayer.presentation.viewmodel.NowPlayingViewModel
import com.aceshot.musicplayer.presentation.viewmodel.OnboardingViewModel
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
                    val currentSong by nowPlayingViewModel.currentSong.collectAsStateWithLifecycle()
                    val isPlaying by nowPlayingViewModel.isPlaying.collectAsStateWithLifecycle()
                    val progress by nowPlayingViewModel.progress.collectAsStateWithLifecycle()

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            Column {
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
                                BottomNavBar(navController)
                            }
                        }
                    ) { innerPadding ->
                        NavGraph(
                            navController = navController,
                            innerPadding = innerPadding
                        )
                    }
                }
            }
        }
    }
}
