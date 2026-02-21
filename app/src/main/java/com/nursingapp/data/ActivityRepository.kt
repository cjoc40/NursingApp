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
        if (customActivities.isNotEmpty()) return
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(ACTIVITIES_KEY, null)

        if (json != null) {
            val type = object : TypeToken<List<ActivityItem>>() {}.type
            val saved: List<ActivityItem> = gson.fromJson(json, type)
            customActivities.addAll(saved)
        }
    }

    fun addActivity(
        context: Context,
        name: String,
        description: String,
        duration: String,
        mobility: MobilityLevel,
        category: ActivityCategory,
        supplies: List<String>
    ) {
        val newItem = ActivityItem(
            id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            name = name,
            description = description,
            duration = duration,
            mobilityRequired = mobility,
            supplies = supplies,
            category = category,
            isCustom = true
        )
        customActivities.add(newItem)
        save(context)
    }
    fun deleteActivity(context: Context, item: ActivityItem) {
        customActivities.remove(item)
        save(context)
    }

    private fun save(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(customActivities.toList())
        prefs.edit().putString(ACTIVITIES_KEY, json).apply()
    }
}