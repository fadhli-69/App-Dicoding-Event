package com.dicoding.aplikasidicodingevent.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface ReminderPreference {
    fun getReminderSetting(): Flow<Boolean>
    suspend fun saveReminderSetting(isEnabled: Boolean)
}

@Singleton
class ReminderPreferenceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ReminderPreference {

    companion object {
        private val REMINDER_KEY = booleanPreferencesKey("reminder_setting")
    }

    override fun getReminderSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[REMINDER_KEY] ?: false
        }
    }

    override suspend fun saveReminderSetting(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[REMINDER_KEY] = isEnabled
        }
    }
}