package com.dicoding.aplikasidicodingevent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dicoding.aplikasidicodingevent.data.preferences.ReminderPreference
import com.dicoding.aplikasidicodingevent.data.preferences.ThemePreference
import com.dicoding.aplikasidicodingevent.worker.ReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val themePreference: ThemePreference,
    private val reminderPreference: ReminderPreference,
    private val workManager: WorkManager
) : ViewModel() {

    val themeSettings = themePreference.getThemeSetting().asLiveData()
    val reminderSettings = reminderPreference.getReminderSetting().asLiveData()

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            themePreference.saveThemeSetting(isDarkModeActive)
        }
    }

    fun saveReminderSetting(isEnabled: Boolean) {
        viewModelScope.launch {
            reminderPreference.saveReminderSetting(isEnabled)
            updateReminderWorker(isEnabled)
        }
    }

    private fun updateReminderWorker(isEnabled: Boolean) {
        if (isEnabled) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            // Jadwalkan untuk jam 8 pagi
            val calendar = Calendar.getInstance().apply {
                if (get(Calendar.HOUR_OF_DAY) >= 8) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            val initialDelay = calendar.timeInMillis - System.currentTimeMillis()

            val reminderRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                1, TimeUnit.DAYS
            )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniquePeriodicWork(
                "event_reminder",
                ExistingPeriodicWorkPolicy.UPDATE,
                reminderRequest
            )
        } else {
            workManager.cancelUniqueWork("event_reminder")
        }
    }
}