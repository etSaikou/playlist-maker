package com.saikou.playlistmaker.domain.api

import com.saikou.playlistmaker.data.track.entity.Track
import com.saikou.playlistmaker.data.track.entity.TrackSearchResponse

interface TrackInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}