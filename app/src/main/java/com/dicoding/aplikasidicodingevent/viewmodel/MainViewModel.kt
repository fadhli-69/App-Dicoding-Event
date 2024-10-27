package com.dicoding.aplikasidicodingevent.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.aplikasidicodingevent.data.remote.ListEventsItem
import com.dicoding.aplikasidicodingevent.utils.Resource
import com.dicoding.aplikasidicodingevent.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: EventRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val lastQuery = savedStateHandle.getStateFlow("last_query", "")
    private val isActiveSearch = savedStateHandle.getStateFlow("is_active_search", true)

    private val _favoriteStatus = MutableStateFlow<Map<Int, Boolean>>(mapOf())
    val favoriteStatus: StateFlow<Map<Int, Boolean>> = _favoriteStatus

    private val _activeEvents = MutableStateFlow<Resource<List<ListEventsItem>>>(Resource.Loading())
    val activeEvents: StateFlow<Resource<List<ListEventsItem>>> = _activeEvents

    private val _finishedEvents = MutableStateFlow<Resource<List<ListEventsItem>>>(Resource.Loading())
    val finishedEvents: StateFlow<Resource<List<ListEventsItem>>> = _finishedEvents

    private val _searchResults = MutableStateFlow<Resource<List<ListEventsItem>>?>(null)
    val searchResults: StateFlow<Resource<List<ListEventsItem>>?> = _searchResults

    init {
        fetchActiveEvents()
        fetchFinishedEvents()
        viewModelScope.launch {
            lastQuery.collect { query ->
                if (query.isNotEmpty()) {
                    searchEvents(query, isActiveSearch.value)
                }
            }
        }
    }

    private fun fetchActiveEvents() {
        viewModelScope.launch {
            repository.getActiveEvents().collect { result ->
                _activeEvents.value = result
            }
        }
    }

    private fun fetchFinishedEvents() {
        viewModelScope.launch {
            repository.getFinishedEvents().collect { result ->
                _finishedEvents.value = result
            }
        }
    }

    fun searchEvents(query: String, isActive: Boolean) {
        savedStateHandle["last_query"] = query
        savedStateHandle["is_active_search"] = isActive

        viewModelScope.launch {
            repository.searchEvents(query, isActive).collect {
                _searchResults.value = it
            }
        }
    }

    fun resetSearch() {
        savedStateHandle["last_query"] = ""
        _searchResults.value = null
    }
    fun updateFavoriteStatuses(events: List<ListEventsItem>) {
        viewModelScope.launch {
            events.forEach { event ->
                event.id?.let { id ->
                    repository.isEventFavorited(id).collect { isFavorite ->
                        event.isBookmarked = isFavorite
                        _favoriteStatus.value += (id to isFavorite)
                    }
                }
            }
        }
    }
}