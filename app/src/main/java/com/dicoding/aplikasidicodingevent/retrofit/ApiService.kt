package com.dicoding.aplikasidicodingevent.retrofit

import com.dicoding.aplikasidicodingevent.data.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvents(
        @Query("active") active: Int
    ): Call<EventResponse>
}
