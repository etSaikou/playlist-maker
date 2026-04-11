package com.saikou.playlistmaker.search.data.local

import com.saikou.playlistmaker.search.data.entity.TrackHistoryDto

interface SearchHistoryStorage {
    fun getTracksHistory() : List<TrackHistoryDto>
    fun addTrack(track: TrackHistoryDto)
    fun clearHistory()
}