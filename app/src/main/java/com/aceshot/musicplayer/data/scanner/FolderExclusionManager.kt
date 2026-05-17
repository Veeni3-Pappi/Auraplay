package com.aceshot.musicplayer.data.scanner

import com.aceshot.musicplayer.data.database.ExcludedFolderDao
import com.aceshot.musicplayer.data.database.ExcludedFolderEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderExclusionManager @Inject constructor(
    private val excludedFolderDao: ExcludedFolderDao
) {
    companion object {
        val DEFAULT_EXCLUSIONS = listOf(
            "WhatsApp/Media/WhatsApp Audio",
            "WhatsApp/Media/WhatsApp Voice Notes",
            "Recordings/Call",
            "Call Recordings",
            "Voice Recorder",
            "Alarms",
            "Ringtones",
            "Notifications",
            "Android/media/com.whatsapp"
        )
    }

    suspend fun detectAndSeedExclusions() {
        if (excludedFolderDao.getCount() == 0) {
            val entities = DEFAULT_EXCLUSIONS.map { path ->
                ExcludedFolderEntity(
                    path = path,
                    isAutoExcluded = true,
                    isEnabled = true,
                    displayName = path.substringAfterLast("/")
                )
            }
            excludedFolderDao.insertAll(entities)
        }
    }

    suspend fun getExcludedPaths(): Set<String> {
        return excludedFolderDao.getEnabledExcludedFolders().map { it.path }.toSet()
    }
}
