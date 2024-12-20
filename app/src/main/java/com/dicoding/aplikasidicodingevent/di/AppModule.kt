package com.dicoding.aplikasidicodingevent.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.work.WorkManager
import com.dicoding.aplikasidicodingevent.data.local.EventDao
import com.dicoding.aplikasidicodingevent.data.local.EventDatabase
import com.dicoding.aplikasidicodingevent.data.preferences.ReminderPreference
import com.dicoding.aplikasidicodingevent.data.preferences.ReminderPreferenceImpl
import com.dicoding.aplikasidicodingevent.data.repository.EventRepository
import com.dicoding.aplikasidicodingevent.data.repository.EventRepositoryImpl
import com.dicoding.aplikasidicodingevent.retrofit.ApiConfig
import com.dicoding.aplikasidicodingevent.retrofit.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return ApiConfig.create()
    }

    @Provides
    @Singleton
    fun provideEventDatabase(
        @ApplicationContext context: Context
    ): EventDatabase {
        return Room.databaseBuilder(
            context,
            EventDatabase::class.java,
            "event_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideEventDao(database: EventDatabase) = database.eventDao()

    @Provides
    @Singleton
    fun provideEventRepository(
        apiService: ApiService,
        eventDao: EventDao
    ): EventRepository = EventRepositoryImpl(apiService, eventDao)

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.dataStore

    @Provides
    @Singleton
    fun provideReminderPreference(dataStore: DataStore<Preferences>): ReminderPreference {
        return ReminderPreferenceImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}