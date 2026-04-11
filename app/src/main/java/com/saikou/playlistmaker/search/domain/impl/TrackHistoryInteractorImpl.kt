package com.saikou.playlistmaker.search.domain.impl

import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.search.domain.HistoryRepository
import com.saikou.playlistmaker.search.domain.TrackHistoryInteractor

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