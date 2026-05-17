package com.aceshot.musicplayer.data.repository

import com.aceshot.musicplayer.data.preferences.RepeatMode
import com.aceshot.musicplayer.data.preferences.ThemeMode
import com.aceshot.musicplayer.data.preferences.UserPreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val prefsManager: UserPreferencesManager
) {
    val themeMode: Flow<ThemeMode> = prefsManager.preferencesFlow.map { it.themeMode }
    val hasCompletedOnboarding: Flow<Boolean> = prefsManager.preferencesFlow.map { it.hasCompletedOnboarding }
    val shuffleEnabled: Flow<Boolean> = prefsManager.preferencesFlow.map { it.shuffleEnabled }
    val repeatMode: Flow<RepeatMode> = prefsManager.preferencesFlow.map { it.repeatMode }
    val crossfadeDurationMs: Flow<Int> = prefsManager.preferencesFlow.map { it.crossfadeDurationMs }
    val minDurationFilterMs: Flow<Long> = prefsManager.preferencesFlow.map { it.minDurationFilterMs }

    suspend fun setThemeMode(mode: ThemeMode) = prefsManager.updateThemeMode(mode)
    suspend fun setOnboardingCompleted(completed: Boolean) = prefsManager.updateHasCompletedOnboarding(completed)
    suspend fun setShuffleEnabled(enabled: Boolean) = prefsManager.updateShuffleEnabled(enabled)
    suspend fun setRepeatMode(mode: RepeatMode) = prefsManager.updateRepeatMode(mode)
    suspend fun setCrossfadeDuration(durationMs: Int) = prefsManager.updateCrossfadeDuration(durationMs)
    suspend fun setMinDurationFilter(durationMs: Long) = prefsManager.updateMinDurationFilter(durationMs)
}
