package com.saikou.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saikou.playlistmaker.global.Const
import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.search.data.entity.TrackState
import com.saikou.playlistmaker.search.domain.TrackHistoryInteractor
import com.saikou.playlistmaker.search.domain.TrackInteractor
import com.saikou.playlistmaker.util.SingleLiveEvent
import com.saikou.playlistmaker.util.debounce
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val trackHistoryInteractor: TrackHistoryInteractor
) : ViewModel() {

    private val searchState = MutableLiveData<TrackState>()
    private val searchHistory = MutableLiveData<List<Track>?>(null)
    private var latestSearchText: String? = null
    private val showToast = SingleLiveEvent<String?>()


    private val trackSearchDebounce =
        debounce<String>(Const.SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            searchRequest(changedText)
        }

    fun searchDebounce(changedText: String, isRefresh: Boolean) {

        if (latestSearchText == changedText && !isRefresh) {
            return
        }

        this.latestSearchText = changedText
        trackSearchDebounce(changedText)
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {

            renderState(TrackState.Loading)

            viewModelScope.launch {
                trackInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second?.first(), pair.second?.last())
                    }
            }
        } else {
            clearSearch()
        }
    }

    private fun processResult(
        foundTracks: List<Track>?,
        errorMessage: String?,
        additionalMessage: String?
    ) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
                renderState(
                    TrackState.Error(
                        errorMessage
                    )
                )

                if (!additionalMessage.isNullOrEmpty()) showToast.postValue(
                    additionalMessage
                )

            }

            tracks.isEmpty() -> {
                renderState(
                    TrackState.Empty(
                        trackInteractor.sendEmptyMessage().toString()
                    )
                )
            }

            else -> {
                renderState(
                    TrackState.Content(
                        tracks = tracks
                    )
                )
            }
        }
    }

    private fun renderState(state: TrackState) {
        searchState.postValue(state)
    }

    fun observeState(): LiveData<TrackState> {
        return searchState
    }

    fun observeShowToast(): LiveData<String?> = showToast

    private fun postHistory() {
        viewModelScope.launch {
            trackHistoryInteractor.getHistory().collect { history ->
                searchHistory.postValue(history)
            }
        }
    }

    fun clearSearch() {
        renderState(TrackState.Loading)

        viewModelScope.launch {
            trackHistoryInteractor.getHistory().collect { history ->
                searchHistory.value = history
                renderState(TrackState.History(history))
            }
        }

    }

    fun clearHistory() {

        trackHistoryInteractor.clearHistory()
        searchHistory.postValue(emptyList())
        renderState(TrackState.Content(emptyList()))
    }

    fun addToHistory(track: Track) {
        viewModelScope.launch {
            trackHistoryInteractor.addTrack(track)
        }
        postHistory()
    }

}