package com.saikou.playlistmaker.media_libr.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saikou.playlistmaker.media_libr.domain.FavoriteInteractor
import com.saikou.playlistmaker.media_libr.ui.models.FavoriteState
import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.search.domain.TrackHistoryInteractor
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val favoriteInteractor: FavoriteInteractor,
    private val trackHistoryInteractor: TrackHistoryInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteState>()

    init {
        fillData()
    }

    fun observeState(): LiveData<FavoriteState> = stateLiveData

    private fun fillData() {
        viewModelScope.launch {
            favoriteInteractor
                .getFavoriteTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            stateLiveData.postValue(FavoriteState.Empty)
        } else {
            stateLiveData.postValue(FavoriteState.Content(tracks))
        }
    }

    fun addToHistory(track: Track) {
        viewModelScope.launch {
            trackHistoryInteractor.addTrack(track)
        }
    }
}
