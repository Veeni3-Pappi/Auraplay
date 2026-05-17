package com.aceshot.musicplayer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aceshot.musicplayer.data.model.Song
import com.aceshot.musicplayer.data.preferences.RepeatMode
import com.aceshot.musicplayer.data.repository.SettingsRepository
import com.aceshot.musicplayer.player.QueueManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val queueManager: QueueManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val currentQueue = queueManager.currentQueue
    val currentIndex = queueManager.currentIndex

    val currentSong = combine(currentQueue, currentIndex) { queue, index ->
        if (queue.isNotEmpty() && index in queue.indices) queue[index] else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    val shuffleEnabled = settingsRepository.shuffleEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val repeatMode = settingsRepository.repeatMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RepeatMode.OFF)

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun skipNext() {
        queueManager.updateIndex(currentIndex.value + 1)
    }

    fun skipPrevious() {
        queueManager.updateIndex(currentIndex.value - 1)
    }

    fun toggleShuffle() {
        viewModelScope.launch {
            settingsRepository.setShuffleEnabled(!shuffleEnabled.value)
        }
    }

    fun toggleRepeat() {
        viewModelScope.launch {
            val nextMode = when (repeatMode.value) {
                RepeatMode.OFF -> RepeatMode.ALL
                RepeatMode.ALL -> RepeatMode.ONE
                RepeatMode.ONE -> RepeatMode.OFF
            }
            settingsRepository.setRepeatMode(nextMode)
        }
    }

    fun seekTo(fraction: Float) {
        _progress.value = fraction
    }
}
