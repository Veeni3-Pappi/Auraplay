// FEATURE: Music Library & Scanning — Room database definition
package com.aceshot.musicplayer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SongEntity::class,
        SongFts::class,
        PlaylistEntity::class,
        PlaylistSongCrossRef::class,
        ExcludedFolderEntity::class,
        RecentSearchEntity::class,
        QueueEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AuraplayDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun excludedFolderDao(): ExcludedFolderDao
    abstract fun recentSearchDao(): RecentSearchDao
    abstract fun queueDao(): QueueDao
}
