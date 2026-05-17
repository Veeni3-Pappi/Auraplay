package com.aceshot.musicplayer.player

import com.aceshot.musicplayer.data.database.QueueDao
import com.aceshot.musicplayer.data.database.QueueEntity
import com.aceshot.musicplayer.data.model.Song
import com.aceshot.musicplayer.data.repository.MusicRepository
import com.aceshot.musicplayer.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueueManager @Inject constructor(
    private val queueDao: QueueDao,
    private val musicRepository: MusicRepository,
    private val settingsRepository: SettingsRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _currentQueue = MutableStateFlow<List<Song>>(emptyList())
    val currentQueue: StateFlow<List<Song>> = _currentQueue.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    init {
        scope.launch {
            restoreQueue()
        }
    }

    private suspend fun restoreQueue() = withContext(Dispatchers.IO) {
        val entities = queueDao.getQueue()
        if (entities.isNotEmpty()) {
            val allSongs = musicRepository.getAllSongs().first()
            val songMap = allSongs.associateBy { it.id }
            
            val queue = entities.mapNotNull { songMap[it.songId] }
            _currentQueue.value = queue
            if (queue.isNotEmpty()) {
                _currentIndex.value = 0
            }
        }
    }

    suspend fun saveQueue() = withContext(Dispatchers.IO) {
        queueDao.clearQueue()
        val entities = _currentQueue.value.mapIndexed { index, song ->
            QueueEntity(songId = song.id, position = index)
        }
        queueDao.insertAll(entities)
    }

    fun setQueue(songs: List<Song>, startIndex: Int) {
        _currentQueue.value = songs
        _currentIndex.value = if (songs.isNotEmpty() && startIndex in songs.indices) startIndex else if (songs.isNotEmpty()) 0 else -1
        scope.launch { saveQueue() }
    }

    fun updateIndex(index: Int) {
        if (index in _currentQueue.value.indices) {
            _currentIndex.value = index
        }
    }

    fun addNext(song: Song) {
        val current = _currentQueue.value.toMutableList()
        val index = if (_currentIndex.value != -1) _currentIndex.value + 1 else 0
        current.add(index, song)
        _currentQueue.value = current
        scope.launch { saveQueue() }
    }

    fun addToEnd(song: Song) {
        val current = _currentQueue.value.toMutableList()
        current.add(song)
        _currentQueue.value = current
        if (_currentIndex.value == -1) {
            _currentIndex.value = 0
        }
        scope.launch { saveQueue() }
    }
}
