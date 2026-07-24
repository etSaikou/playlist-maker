package com.saikou.playlistmaker.search.domain

import com.saikou.playlistmaker.search.data.entity.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun searchTracks(expression: String): Flow<Pair<List<Track>?, List<String?>?>>
    fun sendEmptyMessage(): String?

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?, additionalMessage: String?)
    }
}