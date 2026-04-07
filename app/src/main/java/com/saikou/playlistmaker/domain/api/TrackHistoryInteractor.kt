package com.saikou.playlistmaker.domain.api

import com.saikou.playlistmaker.data.track.entity.Track

interface TrackHistoryInteractor {

    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()

}