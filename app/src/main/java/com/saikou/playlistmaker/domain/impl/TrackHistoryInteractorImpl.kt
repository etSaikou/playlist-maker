package com.saikou.playlistmaker.domain.impl

import com.saikou.playlistmaker.data.track.entity.Track
import com.saikou.playlistmaker.domain.api.HistoryRepository
import com.saikou.playlistmaker.domain.api.TrackHistoryInteractor

class TrackHistoryInteractorImpl(private val repository: HistoryRepository): TrackHistoryInteractor {
    override fun getHistory(): List<Track> {
        return repository.getTracksHistory()
    }

    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}