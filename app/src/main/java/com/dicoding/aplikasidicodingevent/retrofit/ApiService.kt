package com.dicoding.aplikasidicodingevent.retrofit

import com.dicoding.aplikasidicodingevent.data.remote.EventResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    // Endpoint untuk list biasa
    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int
    ): EventResponse

    // Endpoint khusus untuk reminder, menggunakan limit=1
    @GET("events")
    suspend fun getUpcomingEvent(
        @Query("active") active: Int = 1,
        @Query("limit") limit: Int = 1
    ): EventResponse
}