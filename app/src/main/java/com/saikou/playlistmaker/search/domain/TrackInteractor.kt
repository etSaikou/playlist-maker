package com.saikou.playlistmaker.search.domain

import com.saikou.playlistmaker.search.data.entity.Track

interface TrackInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)
    fun sendEmptyMessage(): String?

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?, additionalMessage: String?)
    }
}