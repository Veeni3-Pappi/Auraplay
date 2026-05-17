package com.aceshot.musicplayer.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeMode { SYSTEM, LIGHT, DARK }
enum class RepeatMode { OFF, ONE, ALL }

data class UserPreferences(
    val hasCompletedOnboarding: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val shuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val crossfadeDurationMs: Int = 0,
    val minDurationFilterMs: Long = 60_000L,
    val lastScanTimestamp: Long = 0L
)

@Singleton
class UserPreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SHUFFLE_ENABLED = booleanPreferencesKey("shuffle_enabled")
        val REPEAT_MODE = stringPreferencesKey("repeat_mode")
        val CROSSFADE_DURATION_MS = intPreferencesKey("crossfade_duration_ms")
        val MIN_DURATION_FILTER_MS = longPreferencesKey("min_duration_filter_ms")
        val LAST_SCAN_TIMESTAMP = longPreferencesKey("last_scan_timestamp")
    }

    val preferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            UserPreferences(
                hasCompletedOnboarding = preferences[HAS_COMPLETED_ONBOARDING] ?: false,
                themeMode = ThemeMode.valueOf(preferences[THEME_MODE] ?: ThemeMode.SYSTEM.name),
                shuffleEnabled = preferences[SHUFFLE_ENABLED] ?: false,
                repeatMode = RepeatMode.valueOf(preferences[REPEAT_MODE] ?: RepeatMode.OFF.name),
                crossfadeDurationMs = preferences[CROSSFADE_DURATION_MS] ?: 0,
                minDurationFilterMs = preferences[MIN_DURATION_FILTER_MS] ?: 60_000L,
                lastScanTimestamp = preferences[LAST_SCAN_TIMESTAMP] ?: 0L
            )
        }

    suspend fun updateHasCompletedOnboarding(completed: Boolean) {
        dataStore.edit { it[HAS_COMPLETED_ONBOARDING] = completed }
    }

    suspend fun updateThemeMode(mode: ThemeMode) {
        dataStore.edit { it[THEME_MODE] = mode.name }
    }

    suspend fun updateShuffleEnabled(enabled: Boolean) {
        dataStore.edit { it[SHUFFLE_ENABLED] = enabled }
    }

    suspend fun updateRepeatMode(mode: RepeatMode) {
        dataStore.edit { it[REPEAT_MODE] = mode.name }
    }

    suspend fun updateCrossfadeDuration(durationMs: Int) {
        dataStore.edit { it[CROSSFADE_DURATION_MS] = durationMs }
    }

    suspend fun updateMinDurationFilter(durationMs: Long) {
        dataStore.edit { it[MIN_DURATION_FILTER_MS] = durationMs }
    }

    suspend fun updateLastScanTimestamp(timestamp: Long) {
        dataStore.edit { it[LAST_SCAN_TIMESTAMP] = timestamp }
    }
}
