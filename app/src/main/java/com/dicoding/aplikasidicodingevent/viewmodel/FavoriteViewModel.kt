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
class FavoriteViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {

    private val _favoriteEvents = MutableStateFlow<Resource<List<ListEventsItem>>>(Resource.Loading())
    val favoriteEvents: StateFlow<Resource<List<ListEventsItem>>> = _favoriteEvents

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    init {
        getFavoriteEvents()
    }

    private fun getFavoriteEvents() {
        viewModelScope.launch {
            repository.getFavoriteEvents().collect { result ->
                _favoriteEvents.value = result
            }
        }
    }

    fun checkFavoriteStatus(id: Int) {
        viewModelScope.launch {
            repository.isEventFavorited(id).collect { status ->
                _isFavorite.value = status
            }
        }
    }

    fun toggleFavorite(event: ListEventsItem) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                repository.removeFromFavorite(event)
            } else {
                repository.addToFavorite(event)
            }
        }
    }
}