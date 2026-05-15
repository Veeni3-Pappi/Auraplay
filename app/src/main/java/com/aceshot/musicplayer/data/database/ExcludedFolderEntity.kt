// FEATURE: Smart Folder Exclusion — Excluded folder entity
package com.aceshot.musicplayer.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "excluded_folders")
data class ExcludedFolderEntity(
    @PrimaryKey val path: String,
    val isAutoExcluded: Boolean = true,
    val isEnabled: Boolean = true,
    val displayName: String = ""
)
