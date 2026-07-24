package com.saikou.playlistmaker.search.domain

import com.saikou.playlistmaker.search.data.entity.Track
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getTracksHistory() : Flow<List<Track>>
    suspend fun addTrack(track: Track)
    suspend fun clearHistory()
}