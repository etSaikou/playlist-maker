package com.saikou.playlistmaker.search.data.entity

sealed interface TrackState {

    object Loading : TrackState

    object LoadingHistory : TrackState

    data class Content(
        val tracks: List<Track>
    ) : TrackState

    data class Error(
        val message: String
    ) : TrackState

    data class Empty(
        val message: String
    ) : TrackState

    data class History(val trackHistory: List<Track>) : TrackState
}