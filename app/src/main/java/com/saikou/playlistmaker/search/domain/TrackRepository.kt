package com.saikou.playlistmaker.search.domain

import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.util.Resource

interface TrackRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
    fun emptyMessage(): String?
}