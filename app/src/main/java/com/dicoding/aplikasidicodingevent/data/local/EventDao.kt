package com.dicoding.aplikasidicodingevent.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM favorite_events")
    fun getAllFavoriteEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM favorite_events WHERE id = :id")
    fun getFavoriteEventById(id: Int): Flow<EventEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteEvent(event: EventEntity)

    @Delete
    suspend fun deleteFavoriteEvent(event: EventEntity)

    @Query("SELECT EXISTS(SELECT * FROM favorite_events WHERE id = :id)")
    fun isEventFavorited(id: Int): Flow<Boolean>
}