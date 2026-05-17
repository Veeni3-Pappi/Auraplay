package com.aceshot.musicplayer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aceshot.musicplayer.data.preferences.ThemeMode
import com.aceshot.musicplayer.data.repository.MusicRepository
import com.aceshot.musicplayer.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val musicRepository: MusicRepository
) : ViewModel() {

    val themeMode = settingsRepository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val minDurationFilterMs = settingsRepository.minDurationFilterMs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 60000L)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setMinDurationFilter(durationMs: Long) {
        viewModelScope.launch {
            settingsRepository.setMinDurationFilter(durationMs)
        }
    }

    fun rescanLibrary() {
        viewModelScope.launch {
            musicRepository.scanAndSync()
        }
    }
}
