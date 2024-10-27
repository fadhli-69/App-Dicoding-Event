package com.dicoding.aplikasidicodingevent.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dicoding.aplikasidicodingevent.data.ListEventsItem

@Entity(tableName = "favorite_events")
data class EventEntity(
    @PrimaryKey
    val id: Int,
    val summary: String?,
    val mediaCover: String?,
    val registrants: Int?,
    val imageLogo: String?,
    val link: String?,
    val description: String?,
    val ownerName: String?,
    val cityName: String?,
    val quota: Int?,
    val name: String?,
    val beginTime: String?,
    val endTime: String?,
    val category: String?,
    var isBookmarked: Boolean = false
) {
    fun toListEventsItem(): ListEventsItem {
        return ListEventsItem(
            id = id,
            summary = summary,
            mediaCover = mediaCover,
            registrants = registrants,
            imageLogo = imageLogo,
            link = link,
            description = description,
            ownerName = ownerName,
            cityName = cityName,
            quota = quota,
            name = name,
            beginTime = beginTime,
            endTime = endTime,
            category = category,
            isBookmarked = isBookmarked
        )
    }

    companion object {
        fun fromListEventsItem(event: ListEventsItem): EventEntity {
            return EventEntity(
                id = event.id ?: 0,
                summary = event.summary,
                mediaCover = event.mediaCover,
                registrants = event.registrants,
                imageLogo = event.imageLogo,
                link = event.link,
                description = event.description,
                ownerName = event.ownerName,
                cityName = event.cityName,
                quota = event.quota,
                name = event.name,
                beginTime = event.beginTime,
                endTime = event.endTime,
                category = event.category,
                isBookmarked = event.isBookmarked
            )
        }
    }
}