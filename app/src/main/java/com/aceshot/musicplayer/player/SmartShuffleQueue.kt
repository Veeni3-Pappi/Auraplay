package com.aceshot.musicplayer.player

import androidx.media3.exoplayer.source.DefaultShuffleOrder
import androidx.media3.exoplayer.source.ShuffleOrder
import com.aceshot.musicplayer.data.model.Song
import java.util.Random

object SmartShuffleQueue {
    
    fun create(
        songs: List<Song>,
        recentHistoryIds: Set<Long> = emptySet(),
        randomSeed: Long = Random().nextLong()
    ): ShuffleOrder {
        if (songs.isEmpty()) return DefaultShuffleOrder(0, randomSeed)
        
        val random = Random(randomSeed)
        val length = songs.size
        
        // If library is very small, fallback to standard random shuffle
        if (length <= 3) {
            val indices = IntArray(length) { it }
            for (i in length - 1 downTo 1) {
                val j = random.nextInt(i + 1)
                val temp = indices[i]
                indices[i] = indices[j]
                indices[j] = temp
            }
            return DefaultShuffleOrder(indices, randomSeed)
        }

        val availableIndices = MutableList(length) { it }
        val resultIndices = IntArray(length)
        
        var lastArtist = ""
        
        for (i in 0 until length) {
            // Recency buffer constraint: try to avoid the last 40% recently played
            val candidates = if (i < length / 2 && recentHistoryIds.isNotEmpty()) {
                availableIndices.filter { idx -> !recentHistoryIds.contains(songs[idx].id) }
            } else {
                availableIndices
            }
            
            val validCandidates = candidates.ifEmpty { availableIndices }
            
            // Artist spread constraint: avoid playing same artist back-to-back
            val artistCandidates = validCandidates.filter { idx -> songs[idx].artist != lastArtist }
            
            val finalCandidates = artistCandidates.ifEmpty { validCandidates }
            
            // Pick random from the valid filtered candidates
            val pickedIndex = finalCandidates[random.nextInt(finalCandidates.size)]
            
            resultIndices[i] = pickedIndex
            availableIndices.remove(Integer.valueOf(pickedIndex)) // remove object, not index
            lastArtist = songs[pickedIndex].artist
        }
        
        // Delegate to Media3's robust DefaultShuffleOrder for clone/insert handling
        return DefaultShuffleOrder(resultIndices, randomSeed)
    }
}
