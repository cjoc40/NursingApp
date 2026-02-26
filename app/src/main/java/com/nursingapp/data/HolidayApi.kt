package com.nursingapp.data

import androidx.compose.runtime.mutableStateListOf

object HolidayRepository {
    private val holidays = mutableStateListOf<SpecialDay>()

    // Example using a public API to get "fun" holidays
    suspend fun fetchHolidaysForMonth(month: Int) {
        try {
            // Logic to call your Holiday API
            // For now, let's seed it with some automatic "Senior Friendly" days
            if (holidays.isEmpty()) {
                holidays.addAll(listOf(
                    SpecialDay("National Oreo Day", "03-06", "Fun"),
                    SpecialDay("Pi Day", "03-14", "Educational"),
                    SpecialDay("St. Patrick's Day", "03-17", "Holiday"),
                    SpecialDay("World Poetry Day", "03-21", "Creative")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getHolidayForDate(date: String): SpecialDay? {
        // date format: "MM-dd"
        return holidays.find { it.date == date }
    }
}