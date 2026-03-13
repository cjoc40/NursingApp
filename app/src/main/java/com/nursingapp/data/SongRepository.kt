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
    val songs = mutableStateListOf<QuizItem>()

    private val api: TriviaApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TriviaApiService::class.java)
    }


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

    suspend fun fetchNewTrivia(
        context: Context,
        categoryId: Int? = null,
        difficulty: String? = null
    ) {
        try {
            val response = api.getQuestions(
                amount = 5,
                category = categoryId,
                difficulty = difficulty
            )

            val newItems = response.results.map { result ->
                val decodedQuestion = Html.fromHtml(result.question, Html.FROM_HTML_MODE_LEGACY).toString()
                val decodedAnswer = Html.fromHtml(result.correct_answer, Html.FROM_HTML_MODE_LEGACY).toString()

                val hintText = if (result.type == "multiple") {
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
                    apiCategoryId = categoryId ?: getCategoryIdFromName(result.category),
                    isCustom = true
                )
            }

            songs.addAll(newItems)
            saveToPrefs(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Helper to map API category names to the IDs used in Filter chips
    private fun getCategoryIdFromName(name: String): Int? {
        return when {
            name.contains("General", ignoreCase = true) -> 9
            name.contains("Music", ignoreCase = true) -> 12
            name.contains("Science", ignoreCase = true) -> 17
            name.contains("Sports", ignoreCase = true) -> 21
            name.contains("Geography", ignoreCase = true) -> 22
            name.contains("History", ignoreCase = true) -> 23
            name.contains("Animals", ignoreCase = true) -> 27
            else -> null
        }
    }

    fun addSong(
        context: Context,
        title: String,
        artist: String,
        youtubeLink: String,
        lyrics: String
    ) {
        val videoId = when {
            youtubeLink.contains("v=") -> youtubeLink.substringAfter("v=").substringBefore("&")
            youtubeLink.contains("youtu.be/") -> youtubeLink.substringAfter("youtu.be/").substringBefore("?")
            youtubeLink.contains("shorts/") -> youtubeLink.substringAfter("shorts/").substringBefore("?")
            else -> youtubeLink.trim()
        }

        val newSong = QuizItem(
            id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            question = "",
            answer = "$title – $artist",
            category = QuizCategory.GUESS_THE_SONG,
            youtubeId = videoId,
            lyrics = "♪ \"$lyrics…\"",
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