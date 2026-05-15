// FEATURE: Smart Folder Exclusion — Excluded folder data access
package com.aceshot.musicplayer.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExcludedFolderDao {
    @Query("SELECT * FROM excluded_folders ORDER BY isAutoExcluded DESC, path ASC")
    fun getAllExcludedFolders(): Flow<List<ExcludedFolderEntity>>

    @Query("SELECT * FROM excluded_folders WHERE isEnabled = 1")
    suspend fun getEnabledExcludedFolders(): List<ExcludedFolderEntity>

    @Query("SELECT COUNT(*) FROM excluded_folders WHERE isAutoExcluded = 1 AND isEnabled = 1")
    fun getAutoExcludedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(folders: List<ExcludedFolderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: ExcludedFolderEntity)

    @Update
    suspend fun update(folder: ExcludedFolderEntity)

    @Delete
    suspend fun delete(folder: ExcludedFolderEntity)

    @Query("SELECT COUNT(*) FROM excluded_folders")
    suspend fun getCount(): Int
}
