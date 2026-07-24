package com.saikou.playlistmaker.search.data.local

import com.saikou.playlistmaker.search.data.entity.TrackHistoryDto

interface SearchHistoryStorage {
    suspend fun getTracksHistory() : List<TrackHistoryDto>
    suspend fun addTrack(track: TrackHistoryDto)
    suspend fun clearHistory()
}