package com.nursingapp.data

import retrofit2.http.GET
import retrofit2.http.Query

data class TriviaResponse(val results: List<TriviaResult>)

interface TriviaApiService {
    @GET("api.php")
    suspend fun getTrivia(
        @Query("amount") amount: Int,
        // Removing the 'type' query entirely allows for a mix of everything
    ): TriviaResponse
}

// Add incorrect_answers to your data model so we can use them
data class TriviaResult(
    val category: String,
    val type: String, // "multiple" or "boolean"
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String> // New field
)