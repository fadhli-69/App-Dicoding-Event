package com.dicoding.aplikasidicodingevent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.aplikasidicodingevent.data.ListEventsItem
import com.dicoding.aplikasidicodingevent.data.Resource
import com.dicoding.aplikasidicodingevent.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {

    private val _activeEvents = MutableStateFlow<Resource<List<ListEventsItem>>>(Resource.Loading())
    val activeEvents: StateFlow<Resource<List<ListEventsItem>>> = _activeEvents

    private val _finishedEvents = MutableStateFlow<Resource<List<ListEventsItem>>>(Resource.Loading())
    val finishedEvents: StateFlow<Resource<List<ListEventsItem>>> = _finishedEvents

    private val _searchResults = MutableStateFlow<Resource<List<ListEventsItem>>>(Resource.Loading())
    val searchResults: StateFlow<Resource<List<ListEventsItem>>> = _searchResults

    init {
        fetchActiveEvents()
        fetchFinishedEvents()
    }

    private fun fetchActiveEvents() {
        viewModelScope.launch {
            repository.getActiveEvents().collect {
                _activeEvents.value = it
            }
        }
    }

    private fun fetchFinishedEvents() {
        viewModelScope.launch {
            repository.getFinishedEvents().collect {
                _finishedEvents.value = it
            }
        }
    }

    fun searchEvents(query: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.searchEvents(query, isActive).collect {
                _searchResults.value = it
            }
        }
    }

    fun resetSearch(isActive: Boolean) {
        viewModelScope.launch {
            if (isActive) {
                repository.getActiveEvents().collect {
                    _searchResults.value = it
                }
            } else {
                repository.getFinishedEvents().collect {
                    _searchResults.value = it
                }
            }
        }
    }
}