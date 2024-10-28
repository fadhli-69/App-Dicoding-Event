package com.dicoding.aplikasidicodingevent.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dicoding.aplikasidicodingevent.R
import com.dicoding.aplikasidicodingevent.data.preferences.ReminderPreference
import com.dicoding.aplikasidicodingevent.data.repository.EventRepository
import com.dicoding.aplikasidicodingevent.utils.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: EventRepository,
    private val reminderPreference: ReminderPreference
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val isEnabled = reminderPreference.getReminderSetting().first()

            if (isEnabled) {
                // Menggunakan fungsi khusus untuk reminder dengan limit=1
                val events = repository.getUpcomingEventForReminder().first()

                if (events is Resource.Success && events.data?.isNotEmpty() == true) {
                    val upcomingEvent = events.data.first()

                    val formattedTime = formatDateTime(upcomingEvent.beginTime.orEmpty())

                    showNotification(
                        upcomingEvent.name.orEmpty(),
                        formattedTime
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun formatDateTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

            val date = inputFormat.parse(dateTime)
            outputFormat.format(date as Date)
        } catch (e: Exception) {
            dateTime
        }
    }

    private fun showNotification(eventName: String, eventTime: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "event_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Event Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Shows notification for upcoming events"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_event_available_24)
            .setContentTitle("Upcoming Event")
            .setContentText("$eventName starts at $eventTime")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}