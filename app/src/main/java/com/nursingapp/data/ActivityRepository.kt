package com.nursingapp.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ActivityRepository {
    private const val PREFS_NAME = "activity_prefs"
    private const val ACTIVITIES_KEY = "saved_activities"
    private val gson = Gson()

    val customActivities = mutableStateListOf<ActivityItem>()
    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(ACTIVITIES_KEY, null)

        val savedItems: List<ActivityItem> = if (json != null) {
            val type = object : TypeToken<List<ActivityItem>>() {}.type
            val saved: List<ActivityItem> = gson.fromJson(json, type)
            saved.map { item ->
                item.copy(
                    instructions = item.instructions ?: emptyList(),
                    benefits = item.benefits ?: emptyList(),
                    supplies = item.supplies ?: emptyList()
                )
            }
        } else {
            emptyList()
        }

        // Keep built-in activities available, then overlay saved items by id.
        val mergedById = LinkedHashMap<Int, ActivityItem>()
        allActivityItems.forEach { mergedById[it.id] = it }
        savedItems.forEach { mergedById[it.id] = it }

        customActivities.clear()
        customActivities.addAll(mergedById.values)
        save(context)
    }
    private const val REMINDER_TIME_KEY = "reminder_time"

    fun scheduleNotification(context: android.content.Context, item: ActivityItem) {
        if (item.scheduledDate == null) return

        val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = android.content.Intent(context, AlarmReceiver::class.java).apply {
            putExtra("activity_name", item.name)
        }

        val pendingIntent = android.app.PendingIntent.getBroadcast(
            context, item.id, intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(item.scheduledDate)
        val savedTime = getReminderTime(context) // "05:35"
        val hour = savedTime.split(":")[0].toInt()
        val minute = savedTime.split(":")[1].toInt()

        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, hour) // Use the dynamic hour
            set(Calendar.MINUTE, minute)    // Use the dynamic minute
            set(Calendar.SECOND, 0)
        }

        // Don't schedule if the time has already passed
        if (calendar.timeInMillis > System.currentTimeMillis()) {
            alarmManager.setExactAndAllowWhileIdle(
                android.app.AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
    fun getReminderTime(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(REMINDER_TIME_KEY, "09:00") ?: "09:00"
    }

    fun updateGlobalReminderTime(context: Context, time: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(REMINDER_TIME_KEY, time).apply()

        customActivities.filter { it.scheduledDate != null }.forEach {
            scheduleNotification(context, it)
        }
    }
    fun addActivity(
        context: Context,
        name: String,
        description: String,
        duration: String,
        mobility: MobilityLevel,
        category: ActivityCategory,
        supplies: List<String>,
        instructions: List<String>,
        benefits: List<String>,
        date: String? = null
    ) {
        val newItem = ActivityItem(
            id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            name = name,
            description = description,
            duration = duration,
            mobilityRequired = mobility,
            supplies = supplies,
            category = category,
            isCustom = true,
            scheduledDate = date,
            instructions = instructions,
            benefits =benefits
        )
        customActivities.add(newItem)
        save(context)
    }
    fun deleteActivity(context: Context, item: ActivityItem) {
        customActivities.remove(item)
        save(context)
    }
    fun scheduleActivity(context: Context, item: ActivityItem, date: String, time: String) {
        val index = customActivities.indexOfFirst { it.id == item.id }
        val updatedItem = if (index != -1) {
            customActivities[index] = customActivities[index].copy(
                scheduledDate = date,
                scheduledTime = time
            )
            customActivities[index]
        } else {
            val newItem = item.copy(scheduledDate = date, scheduledTime = time)
            customActivities.add(newItem)
            newItem
        }
        save(context)
        scheduleNotification(context, updatedItem) // This still uses the global reminder time logic
    }

    fun unscheduleActivity(context: Context, item: ActivityItem) {
        val customIndex = customActivities.indexOfFirst { it.id == item.id }
        if (customIndex != -1) {
            customActivities[customIndex] = customActivities[customIndex].copy(scheduledDate = null)
            save(context)
        }
    }

    fun hasActivitiesOnDate(dateString: String): Boolean {
        return customActivities.any { it.scheduledDate == dateString }
    }
    private fun save(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(customActivities.toList())
        prefs.edit().putString(ACTIVITIES_KEY, json).apply()
    }
}