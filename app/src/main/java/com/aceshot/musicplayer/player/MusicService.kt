package com.aceshot.musicplayer.player

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.aceshot.musicplayer.data.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaSessionService() {

    @Inject
    lateinit var player: ExoPlayer

    @Inject
    lateinit var queueManager: QueueManager

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private var mediaSession: MediaSession? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            queueManager.updateIndex(player.currentMediaItemIndex)
        }
    }

    override fun onCreate() {
        super.onCreate()
        
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(CustomMediaSessionCallback())
            .build()

        player.addListener(playerListener)

        serviceScope.launch {
            queueManager.currentQueue.collectLatest { songs ->
                if (songs.isNotEmpty()) {
                    val mediaItems = songs.map { song ->
                        MediaItem.Builder()
                            .setMediaId(song.id.toString())
                            .setUri(song.uri)
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setTitle(song.title)
                                    .setArtist(song.artist)
                                    .setAlbumTitle(song.album)
                                    .setArtworkUri(android.net.Uri.parse("content://media/external/audio/albumart/${song.albumId}"))
                                    .build()
                            )
                            .build()
                    }
                    
                    val wasEmpty = player.mediaItemCount == 0
                    player.setMediaItems(mediaItems)
                    
                    if (wasEmpty && queueManager.currentIndex.value >= 0) {
                        player.seekTo(queueManager.currentIndex.value, 0)
                    }
                } else {
                    player.clearMediaItems()
                }
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        player.removeListener(playerListener)
        mediaSession?.run {
            release()
            mediaSession = null
        }
        serviceScope.cancel()
        super.onDestroy()
    }

    private inner class CustomMediaSessionCallback : MediaSession.Callback {
        // Implement custom commands from UI (like add next, favorite) later here
    }
}
