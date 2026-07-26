package com.saikou.playlistmaker.media_libr.domain.impl

import com.saikou.playlistmaker.media_libr.domain.FavoriteInteractor
import com.saikou.playlistmaker.media_libr.domain.FavoriteRepository
import com.saikou.playlistmaker.search.data.entity.Track
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(private val repository: FavoriteRepository) : FavoriteInteractor {
    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        repository.deleteTrack(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }

    override fun getFavoriteTrackIds(): Flow<List<Long>> {
        return repository.getFavoriteTrackIds()
    }
}
