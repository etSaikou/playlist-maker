package com.saikou.playlistmaker.search.domain.impl

import com.saikou.playlistmaker.media_libr.domain.FavoriteInteractor
import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.search.domain.HistoryRepository
import com.saikou.playlistmaker.search.domain.TrackHistoryInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class TrackHistoryInteractorImpl(
    private val repository: HistoryRepository,
    private val favoriteInteractor: FavoriteInteractor,
) : TrackHistoryInteractor {
    override fun getHistory(): Flow<List<Track>> {
        return repository.getTracksHistory().combine(favoriteInteractor.getFavoriteTrackIds()) { history, favoriteIds ->
            history.map { track ->
                track.copy(isFavorite = favoriteIds.contains(track.trackId))
            }
        }
    }

    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun clearHistory() {
        repository.clearHistory()
    }
}