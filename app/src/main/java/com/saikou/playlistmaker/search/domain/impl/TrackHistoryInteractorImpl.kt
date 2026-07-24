package com.saikou.playlistmaker.search.domain.impl

import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.search.domain.HistoryRepository
import com.saikou.playlistmaker.search.domain.TrackHistoryInteractor
import kotlinx.coroutines.flow.Flow

class TrackHistoryInteractorImpl(private val repository: HistoryRepository): TrackHistoryInteractor {
    override fun getHistory(): Flow<List<Track>> {
        return repository.getTracksHistory()
    }

    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun clearHistory() {
        repository.clearHistory()
    }
}