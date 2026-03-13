package com.nursingapp.data

import retrofit2.http.GET
import retrofit2.http.Query

data class TriviaResponse(val results: List<TriviaResult>)

interface TriviaApiService {
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int = 5,
        @Query("category") category: Int? = null,
        @Query("difficulty") difficulty: String? = null
    ): TriviaResponse
}

data class TriviaResult(
    val category: String,
    val type: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)