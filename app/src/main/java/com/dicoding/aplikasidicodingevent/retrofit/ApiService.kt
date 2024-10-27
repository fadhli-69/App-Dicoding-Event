package com.dicoding.aplikasidicodingevent.retrofit

import com.dicoding.aplikasidicodingevent.data.remote.EventResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int
    ): EventResponse
}
