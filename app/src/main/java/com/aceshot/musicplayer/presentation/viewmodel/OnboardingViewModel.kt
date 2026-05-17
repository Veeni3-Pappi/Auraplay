package com.aceshot.musicplayer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aceshot.musicplayer.data.repository.MusicRepository
import com.aceshot.musicplayer.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val musicRepository: MusicRepository
) : ViewModel() {

    val hasCompletedOnboarding = settingsRepository.hasCompletedOnboarding
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun completeOnboarding() {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(true)
        }
    }

    fun scanLibrary() {
        viewModelScope.launch {
            musicRepository.scanAndSync()
        }
    }
}
