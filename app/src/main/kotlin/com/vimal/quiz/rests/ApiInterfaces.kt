package com.vimal.quiz.rests


import com.vimal.quiz.databases.CallbackQuiz
import com.vimal.quiz.utils.Config
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiInterfaces {

    @Headers(CACHE, AGENT)
    @GET("api/getQuiz/?api_key=$API_KEY")
    fun getQuiz(
        @Query("amount") amount: Int,
        @Query("category") category: String?
    ): Call<CallbackQuiz>


    companion object {
        const val CACHE = "Cache-Control: max-age=640000"
        const val AGENT = "User-Agent: Quiz App"
        const val API_KEY = Config.API_KEY
    }
}
