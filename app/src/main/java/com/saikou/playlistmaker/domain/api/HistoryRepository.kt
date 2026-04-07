package com.saikou.playlistmaker.domain.api

import com.saikou.playlistmaker.data.track.entity.Track

interface HistoryRepository {
    fun getTracksHistory() : List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}