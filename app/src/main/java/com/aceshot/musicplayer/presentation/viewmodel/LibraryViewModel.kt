package com.aceshot.musicplayer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aceshot.musicplayer.data.model.Song
import com.aceshot.musicplayer.data.repository.MusicRepository
import com.aceshot.musicplayer.data.repository.SettingsRepository
import com.aceshot.musicplayer.data.repository.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _sortOrder = MutableStateFlow(SortOrder.TITLE)
    val sortOrder = _sortOrder.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.sortOrder.collect { saved ->
                _sortOrder.value = try { SortOrder.valueOf(saved) } catch (_: Exception) { SortOrder.TITLE }
            }
        }
    }

    val songs = _sortOrder
        .flatMapLatest { musicRepository.getAllSongs(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val albums = musicRepository.getAlbums()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val artists = musicRepository.getArtists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val genres = musicRepository.getGenres()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val folders = musicRepository.getFolders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _filteredSongs = MutableStateFlow<List<Song>>(emptyList())
    val filteredSongs = _filteredSongs.asStateFlow()

    private val _filterTitle = MutableStateFlow<String?>(null)
    val filterTitle = _filterTitle.asStateFlow()

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        viewModelScope.launch {
            settingsRepository.setSortOrder(order.name)
        }
    }

    fun filterByAlbum(albumId: Long, albumName: String) {
        _filterTitle.value = albumName
        viewModelScope.launch {
            musicRepository.getSongsByAlbum(albumId).collect { _filteredSongs.value = it }
        }
    }

    fun filterByArtist(artist: String) {
        _filterTitle.value = artist
        viewModelScope.launch {
            musicRepository.getSongsByArtist(artist).collect { _filteredSongs.value = it }
        }
    }

    fun filterByGenre(genre: String) {
        _filterTitle.value = genre
        viewModelScope.launch {
            musicRepository.getSongsByGenre(genre).collect { _filteredSongs.value = it }
        }
    }

    fun filterByFolder(folderPath: String) {
        _filterTitle.value = folderPath.substringAfterLast("/")
        viewModelScope.launch {
            musicRepository.getSongsByFolder(folderPath).collect { _filteredSongs.value = it }
        }
    }

    fun clearFilter() {
        _filterTitle.value = null
        _filteredSongs.value = emptyList()
    }
}
