package com.saikou.playlistmaker.media_libr.domain

import com.saikou.playlistmaker.search.data.entity.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {
    suspend fun addTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
    fun getFavoriteTrackIds(): Flow<List<Long>>
}
