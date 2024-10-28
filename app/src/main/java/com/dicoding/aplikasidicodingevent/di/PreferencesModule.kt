package com.dicoding.aplikasidicodingevent.di

import com.dicoding.aplikasidicodingevent.data.preferences.SettingPreferences
import com.dicoding.aplikasidicodingevent.data.preferences.ThemePreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    @Provides
    @Singleton
    fun provideThemePreference(
        settingPreferences: SettingPreferences
    ): ThemePreference = settingPreferences
}