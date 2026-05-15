// FEATURE: Queue Management — Queue persistence data access
package com.aceshot.musicplayer.data.database

import androidx.room.*

@Dao
interface QueueDao {
    @Query("SELECT * FROM queue_items ORDER BY position ASC")
    suspend fun getQueue(): List<QueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<QueueEntity>)

    @Query("DELETE FROM queue_items")
    suspend fun clearQueue()
}
