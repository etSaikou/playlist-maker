package com.saikou.playlistmaker.search.ui.view_model

import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.global.Const
import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.search.data.entity.TrackState
import com.saikou.playlistmaker.search.domain.TrackHistoryInteractor
import com.saikou.playlistmaker.search.domain.TrackInteractor
import com.saikou.playlistmaker.util.SingleLiveEvent

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val trackHistoryInteractor: TrackHistoryInteractor
) : ViewModel() {

    private val searchState = MutableLiveData<TrackState>()
    private val searchHistory = MutableLiveData<List<Track>?>(null)

    private var latestSearchText: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private val showToast = SingleLiveEvent<String?>()

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + Const.SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(
                TrackState.Loading
            )

            trackInteractor.searchTracks(newSearchText, object : TrackInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post {

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

//                                showToast.postValue(errorMessage)

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
                }
            })
        } else {
            handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
            clearSearch()
        }
    }

    private fun renderState(state: TrackState) {
        searchState.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun observeState(): LiveData<TrackState> {
        return searchState
    }

    fun observeShowToast(): LiveData<String?> = showToast

    private fun postHistory() {
        searchHistory.postValue(trackHistoryInteractor.getHistory())
    }

    fun observeHistory(): LiveData<List<Track>?> {
        postHistory()
        return searchHistory
    }

    fun clearSearch() {
        postHistory()
        renderState(TrackState.History(searchHistory.value ?: emptyList()))
    }

    fun clearHistory() {
        trackHistoryInteractor.clearHistory()
        searchHistory.postValue(emptyList())
        renderState(TrackState.Content(emptyList()))
    }

    fun addToHistory(track: Track) {
        trackHistoryInteractor.addTrack(track)
        postHistory()
    }


    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getFactory(
            trackInteractor: TrackInteractor,
            trackHistoryInteractor: TrackHistoryInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(trackInteractor, trackHistoryInteractor)
            }
        }
    }
}