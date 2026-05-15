// FEATURE: Search — Recent search data access
package com.aceshot.musicplayer.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSearchDao {
    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSearches(): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(search: RecentSearchEntity)

    @Query("DELETE FROM recent_searches")
    suspend fun clearAll()

    @Query("DELETE FROM recent_searches WHERE query = :query")
    suspend fun delete(query: String)
}
