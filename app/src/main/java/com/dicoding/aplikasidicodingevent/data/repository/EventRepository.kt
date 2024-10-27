package com.dicoding.aplikasidicodingevent.data.repository

import com.dicoding.aplikasidicodingevent.data.remote.ListEventsItem
import com.dicoding.aplikasidicodingevent.utils.Resource
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getActiveEvents(): Flow<Resource<List<ListEventsItem>>>
    fun getFinishedEvents(): Flow<Resource<List<ListEventsItem>>>
    fun searchEvents(query: String, isActive: Boolean): Flow<Resource<List<ListEventsItem>>>

    // untuk favorite functionality
    fun getFavoriteEvents(): Flow<Resource<List<ListEventsItem>>>
    fun getFavoriteEventById(id: Int): Flow<Resource<ListEventsItem?>>
    fun isEventFavorited(id: Int): Flow<Boolean>
    suspend fun addToFavorite(event: ListEventsItem)
    suspend fun removeFromFavorite(event: ListEventsItem)
}