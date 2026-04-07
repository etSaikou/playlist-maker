package com.saikou.playlistmaker.domain.api

import com.saikou.playlistmaker.data.track.entity.Track
import com.saikou.playlistmaker.data.track.entity.TrackSearchResponse

interface TrackRepository {
    fun searchTracks(expression: String): List<Track>?
}