package com.dicoding.aplikasidicodingevent.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.aplikasidicodingevent.data.EventResponse
import com.dicoding.aplikasidicodingevent.data.ListEventsItem
import com.dicoding.aplikasidicodingevent.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MainViewModel : ViewModel() {

    // Menyimpan data event yang sedang berlangsung (upcoming)
    private val _activeEvents = MutableLiveData<List<ListEventsItem>>()
    val activeEvents: LiveData<List<ListEventsItem>> = _activeEvents

    // Menyimpan data event yang sudah selesai (finished)
    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    // Menyimpan hasil pencarian
    private val _searchResults = MutableLiveData<List<ListEventsItem>>()
    val searchResults: LiveData<List<ListEventsItem>> = _searchResults

    // Menunjukkan apakah sedang dalam proses loading data
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Menyimpan pesan error jika terjadi kesalahan
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    init {
        fetchEvents() // Mengambil semua event (upcoming dan finished)
    }

    // Fungsi untuk mengambil data event (baik upcoming maupun finished)
    fun fetchEvents() {
        _isLoading.value = true
        _errorMessage.value = null

        val clientUpcoming = ApiConfig.create().getEvents(1)
        clientUpcoming.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    _activeEvents.value = response.body()?.listEvents ?: emptyList()
                } else {
                    _errorMessage.value = "Terjadi kesalahan: ${response.message()}"
                }
                checkLoadingComplete()
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                handleError(t)
                checkLoadingComplete()
            }
        })

        val clientFinished = ApiConfig.create().getEvents(0)
        clientFinished.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    _finishedEvents.value = response.body()?.listEvents ?: emptyList()
                } else {
                    _errorMessage.value = "Terjadi kesalahan: ${response.message()}"
                }
                checkLoadingComplete()
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                handleError(t)
                checkLoadingComplete()
            }
        })
    }

    // Fungsi pencarian untuk upcoming dan finished
    fun setSearchQuery(query: String, isUpcoming: Boolean) {
        _searchQuery.value = query
        searchEvents(query, isUpcoming)
    }

    private fun searchEvents(query: String, isUpcoming: Boolean) {
        val validQuery = query.takeIf { it.isNotBlank() } ?: return resetSearch(isUpcoming)

        val filteredEvents = if (isUpcoming) {
            _activeEvents.value?.filter { event ->
                event.name?.contains(validQuery, ignoreCase = true) == true
            } ?: emptyList()
        } else {
            _finishedEvents.value?.filter { event ->
                event.name?.contains(validQuery, ignoreCase = true) == true
            } ?: emptyList()
        }

        _searchResults.value = filteredEvents
    }

    // Fungsi reset hasil pencarian
    fun resetSearch(isUpcoming: Boolean) {
        if (isUpcoming) {
            _searchResults.value = _activeEvents.value
        } else {
            _searchResults.value = _finishedEvents.value
        }
    }

    private fun checkLoadingComplete() {
        if (_activeEvents.value != null && _finishedEvents.value != null) {
            _isLoading.value = false
        }
    }

    private fun handleError(t: Throwable) {
        val message = when (t) {
            is UnknownHostException -> "Maaf, internet Anda lambat atau mati"
            is SocketTimeoutException -> "Koneksi internet Anda terlalu lambat"
            else -> "Terjadi kesalahan: ${t.localizedMessage}"
        }
        _errorMessage.value = message
        Log.e("MainViewModel", "onFailure: ${t.message}")
    }
}

