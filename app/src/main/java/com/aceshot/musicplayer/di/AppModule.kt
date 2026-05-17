package com.aceshot.musicplayer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.aceshot.musicplayer.data.database.AuraplayDatabase
import com.aceshot.musicplayer.data.database.ExcludedFolderDao
import com.aceshot.musicplayer.data.database.PlaylistDao
import com.aceshot.musicplayer.data.database.QueueDao
import com.aceshot.musicplayer.data.database.RecentSearchDao
import com.aceshot.musicplayer.data.database.SongDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuraplayDatabase(
        @ApplicationContext context: Context
    ): AuraplayDatabase {
        return Room.databaseBuilder(
            context,
            AuraplayDatabase::class.java,
            "auraplay_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideSongDao(database: AuraplayDatabase): SongDao = database.songDao()

    @Provides
    @Singleton
    fun providePlaylistDao(database: AuraplayDatabase): PlaylistDao = database.playlistDao()

    @Provides
    @Singleton
    fun provideExcludedFolderDao(database: AuraplayDatabase): ExcludedFolderDao = database.excludedFolderDao()

    @Provides
    @Singleton
    fun provideQueueDao(database: AuraplayDatabase): QueueDao = database.queueDao()

    @Provides
    @Singleton
    fun provideRecentSearchDao(database: AuraplayDatabase): RecentSearchDao = database.recentSearchDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("user_prefs") }
        )
    }
}
