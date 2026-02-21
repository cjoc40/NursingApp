package com.nursingapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SongRepository {
    private const val PREFS_NAME = "quiz_prefs"
    private const val SONGS_KEY = "saved_songs"
    private val gson = Gson()

    // State list for the UI
    val songs = mutableStateListOf<QuizItem>()

    /**
     * Call this in your MainActivity's onCreate or a CompositionEffect
     * to load the songs when the app starts.
     */
    fun initialize(context: Context) {
        if (songs.isNotEmpty()) return // Already loaded

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedSongsJson = prefs.getString(SONGS_KEY, null)

        if (savedSongsJson != null) {
            val type = object : TypeToken<List<QuizItem>>() {}.type
            val savedSongs: List<QuizItem> = gson.fromJson(savedSongsJson, type)
            songs.addAll(savedSongs)
        } else {
            // Load defaults if it's the very first time
            songs.add(
                QuizItem(
                    id = 1,
                    question = "Tap to open in Spotify 🎵",
                    answer = "Hound Dog – Elvis Presley (1956)",
                    category = QuizCategory.GUESS_THE_SONG,
                    spotifyUri = "spotify:track:64Ny7djQ6rNJspquof2KoX"
                )
            )
            saveToPrefs(context)
        }
    }

    fun addSong(context: Context, title: String, artist: String, spotifyLink: String) {
        // Extract Track ID safely
        val trackId = spotifyLink.substringAfter("/track/").substringBefore("?")

        val newSong = QuizItem(
            id = (songs.maxOfOrNull { it.id } ?: 0) + 1,
            question = "Tap to open in Spotify 🎵",
            answer = "$title – $artist",
            category = QuizCategory.GUESS_THE_SONG,
            spotifyUri = "spotify:track:$trackId",
            isCustom = true
        )

        songs.add(newSong)
        saveToPrefs(context)
    }
    fun deleteSong(context: Context, item: QuizItem) {
        songs.remove(item)
        saveToPrefs(context)
    }
    private fun saveToPrefs(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(songs.toList())
        prefs.edit().putString(SONGS_KEY, json).apply()
    }
}