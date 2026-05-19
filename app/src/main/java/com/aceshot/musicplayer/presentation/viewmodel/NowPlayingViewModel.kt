package com.aceshot.musicplayer.presentation.viewmodel

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.aceshot.musicplayer.data.model.Song
import com.aceshot.musicplayer.data.preferences.RepeatMode
import com.aceshot.musicplayer.data.repository.SettingsRepository
import com.aceshot.musicplayer.player.MusicService
import com.aceshot.musicplayer.player.QueueManager
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val queueManager: QueueManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

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

    init {
        connectToService()
        startProgressUpdater()
    }

    private fun connectToService() {
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            mediaController = controllerFuture?.get()
            mediaController?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    val index = mediaController?.currentMediaItemIndex ?: 0
                    queueManager.updateIndex(index)
                }
            })
            _isPlaying.value = mediaController?.isPlaying ?: false
        }, MoreExecutors.directExecutor())
    }

    private fun startProgressUpdater() {
        viewModelScope.launch {
            while (true) {
                delay(500)
                mediaController?.let { controller ->
                    if (controller.duration > 0) {
                        _progress.value = controller.currentPosition.toFloat() / controller.duration.toFloat()
                    }
                }
            }
        }
    }

    fun playSong(songs: List<Song>, startIndex: Int = 0) {
        viewModelScope.launch {
            queueManager.setQueue(songs, startIndex)
            val mediaItems = songs.map { song ->
                MediaItem.Builder()
                    .setMediaId(song.id.toString())
                    .setUri(song.uri)
                    .build()
            }
            mediaController?.let { controller ->
                controller.setMediaItems(mediaItems, startIndex, 0L)
                controller.prepare()
                controller.play()
            }
        }
    }

    fun togglePlayPause() {
        mediaController?.let { controller ->
            if (controller.isPlaying) {
                controller.pause()
            } else {
                controller.play()
            }
        }
    }

    fun skipNext() {
        mediaController?.seekToNextMediaItem()
    }

    fun skipPrevious() {
        mediaController?.seekToPreviousMediaItem()
    }

    fun toggleShuffle() {
        viewModelScope.launch {
            val newValue = !shuffleEnabled.value
            settingsRepository.setShuffleEnabled(newValue)
            mediaController?.shuffleModeEnabled = newValue
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
            mediaController?.repeatMode = when (nextMode) {
                RepeatMode.OFF -> Player.REPEAT_MODE_OFF
                RepeatMode.ALL -> Player.REPEAT_MODE_ALL
                RepeatMode.ONE -> Player.REPEAT_MODE_ONE
            }
        }
    }

    fun seekTo(fraction: Float) {
        mediaController?.let { controller ->
            if (controller.duration > 0) {
                controller.seekTo((fraction * controller.duration).toLong())
            }
        }
        _progress.value = fraction
    }

    override fun onCleared() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        super.onCleared()
    }
}
