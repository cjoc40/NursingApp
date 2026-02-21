package com.nursingapp.data

import android.content.Context
import android.text.Html
import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SongRepository {
    private const val PREFS_NAME = "quiz_prefs"
    private const val SONGS_KEY = "saved_songs"
    private val gson = Gson()

    // Lazily initialize Retrofit to save resources
    private val api: TriviaApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TriviaApiService::class.java)
    }

    // State list for the UI - automatically updates the screen
    val songs = mutableStateListOf<QuizItem>()

    /**
     * Loads saved songs/trivia from SharedPreferences on app start.
     */
    fun initialize(context: Context) {
        if (songs.isNotEmpty()) return

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedSongsJson = prefs.getString(SONGS_KEY, null)

        if (savedSongsJson != null) {
            val type = object : TypeToken<List<QuizItem>>() {}.type
            val savedSongs: List<QuizItem> = gson.fromJson(savedSongsJson, type)
            songs.addAll(savedSongs)
        } else {
            // If no data exists, ensure the file is initialized empty
            saveToPrefs(context)
        }
    }

    /**
     * Fetches a mix of trivia from the OpenTDB API.
     */
    suspend fun fetchNewTrivia(context: Context) {
        try {
            // Amount is set to 5, type is omitted to get a mix of Boolean and Multiple Choice
            val response = api.getTrivia(amount = 5)

            val newItems = response.results.map { result ->
                val decodedQuestion = Html.fromHtml(result.question, Html.FROM_HTML_MODE_LEGACY).toString()
                val decodedAnswer = Html.fromHtml(result.correct_answer, Html.FROM_HTML_MODE_LEGACY).toString()

                // Logic for "Open-Ended" vs "Multiple Choice" support
                val hintText = if (result.type == "multiple") {
                    // Combine correct and incorrect options for the nurse/staff
                    val options = (result.incorrect_answers + result.correct_answer)
                        .map { Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY).toString() }
                        .shuffled()
                        .joinToString(", ")
                    "Options: $options"
                } else {
                    "True or False?"
                }

                QuizItem(
                    id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt() + (0..1000).random(),
                    question = decodedQuestion,
                    answer = decodedAnswer,
                    hint = hintText,
                    category = QuizCategory.TRIVIA,
                    isCustom = true
                )
            }

            songs.addAll(newItems)
            saveToPrefs(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Handles adding a custom Spotify track.
     */
    fun addSong(context: Context, title: String, artist: String, spotifyLink: String) {
        val trackId = when {
            spotifyLink.contains("spotify.com") -> {
                spotifyLink.substringAfter("/track/").substringBefore("?")
            }
            spotifyLink.startsWith("spotify:track:") -> {
                spotifyLink.substringAfterLast(":")
            }
            else -> spotifyLink.trim()
        }

        val newSong = QuizItem(
            id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            question = "Custom song 🎵",
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