package com.dicoding.aplikasidicodingevent.data.repository

import com.dicoding.aplikasidicodingevent.data.remote.ListEventsItem
import com.dicoding.aplikasidicodingevent.utils.Resource
import com.dicoding.aplikasidicodingevent.data.local.EventDao
import com.dicoding.aplikasidicodingevent.data.local.EventEntity
import com.dicoding.aplikasidicodingevent.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao
) : EventRepository {

    override fun getActiveEvents(): Flow<Resource<List<ListEventsItem>>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getEvents(1)
            val events = response.listEvents

            events.forEach { event ->
                event.id?.let { id ->
                    event.isBookmarked = eventDao.isEventFavorited(id).first()
                }
            }

            emit(Resource.Success(events))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan"))
        } catch (e: IOException) {
            emit(Resource.Error("Periksa koneksi internet Anda"))
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan"))
        }
    }

    override fun getFinishedEvents(): Flow<Resource<List<ListEventsItem>>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getEvents(0)
            val events = response.listEvents

            events.forEach { event ->
                event.id?.let { id ->
                    event.isBookmarked = eventDao.isEventFavorited(id).first()
                }
            }

            emit(Resource.Success(events))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan"))
        } catch (e: IOException) {
            emit(Resource.Error("Periksa koneksi internet Anda"))
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan"))
        }
    }

    override fun searchEvents(query: String, isActive: Boolean): Flow<Resource<List<ListEventsItem>>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getEvents(if (isActive) 1 else 0)
            val filteredEvents = response.listEvents.filter {
                it.name?.contains(query, ignoreCase = true) == true
            }

            filteredEvents.forEach { event ->
                event.id?.let { id ->
                    event.isBookmarked = eventDao.isEventFavorited(id).first()
                }
            }

            emit(Resource.Success(filteredEvents))
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan saat mencari event"))
        }
    }

    override fun getFavoriteEvents(): Flow<Resource<List<ListEventsItem>>> =
        eventDao.getAllFavoriteEvents().map { entities ->
            Resource.Success(entities.map {
                it.toListEventsItem().copy(isBookmarked = true)
            })
        }

    override fun isEventFavorited(id: Int): Flow<Boolean> =
        eventDao.isEventFavorited(id)

    override suspend fun addToFavorite(event: ListEventsItem) {
        val eventEntity = EventEntity.fromListEventsItem(event).copy(isBookmarked = true)
        eventDao.insertFavoriteEvent(eventEntity)
    }

    override suspend fun removeFromFavorite(event: ListEventsItem) {
        val eventEntity = EventEntity.fromListEventsItem(event)
        eventDao.deleteFavoriteEvent(eventEntity)
    }

    override fun getUpcomingEventForReminder(): Flow<Resource<List<ListEventsItem>>> = flow {
        try {
            emit(Resource.Loading())
            val response = apiService.getUpcomingEvent() // Menggunakan endpoint dengan limit=1
            emit(Resource.Success(response.listEvents))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan"))
        } catch (e: IOException) {
            emit(Resource.Error("Periksa koneksi internet Anda"))
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan"))
        }
    }
}