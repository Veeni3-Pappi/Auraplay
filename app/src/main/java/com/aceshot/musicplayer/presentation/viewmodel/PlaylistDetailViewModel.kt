package com.aceshot.musicplayer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aceshot.musicplayer.data.database.PlaylistDao
import com.aceshot.musicplayer.data.model.Song
import com.aceshot.musicplayer.data.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val playlistDao: PlaylistDao
) : ViewModel() {

    private val _playlistId = MutableStateFlow(-1L)

    private val _playlistName = MutableStateFlow("")
    val playlistName = _playlistName.asStateFlow()

    val songs = _playlistId
        .flatMapLatest { id ->
            if (id >= 0) playlistRepository.getPlaylistSongs(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadPlaylist(playlistId: Long) {
        _playlistId.value = playlistId
        viewModelScope.launch {
            val playlist = playlistDao.getPlaylistById(playlistId)
            _playlistName.value = playlist?.name ?: "Playlist"
        }
    }

    fun removeSong(songId: Long) {
        viewModelScope.launch {
            playlistRepository.removeSongFromPlaylist(_playlistId.value, songId)
        }
    }
}
