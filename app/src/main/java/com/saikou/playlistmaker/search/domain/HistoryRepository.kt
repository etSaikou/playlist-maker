package com.saikou.playlistmaker.search.domain

import com.saikou.playlistmaker.search.data.entity.Track

interface HistoryRepository {
    fun getTracksHistory() : List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}