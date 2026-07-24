package com.saikou.playlistmaker.search.domain

import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
    fun emptyMessage(): String?
}