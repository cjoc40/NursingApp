package com.nursingapp.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ActivityRepository {
    private const val PREFS_NAME = "activity_prefs"
    private const val ACTIVITIES_KEY = "saved_activities"
    private val gson = Gson()

    val customActivities = mutableStateListOf<ActivityItem>()

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(ACTIVITIES_KEY, null)

        if (json != null) {
            val type = object : TypeToken<List<ActivityItem>>() {}.type
            val saved: List<ActivityItem> = gson.fromJson(json, type)
            customActivities.clear()
            customActivities.addAll(saved)
        } else {
            // If first time opening the app, load the built-in ones into the mutable state
            customActivities.addAll(allActivityItems)
            save(context)
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
            scheduledDate = date
        )
        customActivities.add(newItem)
        save(context)
    }
    fun deleteActivity(context: Context, item: ActivityItem) {
        customActivities.remove(item)
        save(context)
    }
    fun scheduleActivity(context: Context, item: ActivityItem, date: String) {
        val index = customActivities.indexOfFirst { it.id == item.id }
        if (index != -1) {
            customActivities[index] = customActivities[index].copy(scheduledDate = date)
            save(context)
        }
    }
    fun unscheduleActivity(context: Context, item: ActivityItem) {
        // 1. Check custom activities
        val customIndex = customActivities.indexOfFirst { it.id == item.id }
        if (customIndex != -1) {
            customActivities[customIndex] = customActivities[customIndex].copy(scheduledDate = null)
            save(context)
            return
        }
    }
    private fun save(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(customActivities.toList())
        prefs.edit().putString(ACTIVITIES_KEY, json).apply()
    }
}