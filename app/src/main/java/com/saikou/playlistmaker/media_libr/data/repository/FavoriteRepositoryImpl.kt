package com.saikou.playlistmaker.media_libr.data.repository

import com.saikou.playlistmaker.db.AppDatabase
import com.saikou.playlistmaker.db.converter.TrackDbConvertor
import com.saikou.playlistmaker.db.dao.TrackDao
import com.saikou.playlistmaker.media_libr.domain.FavoriteRepository
import com.saikou.playlistmaker.search.data.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavoriteRepository {

    override suspend fun addTrack(track: Track) {
        appDatabase.trackDao().insertTrack(trackDbConvertor.map(track))
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.trackDao().deleteTrack(trackDbConvertor.map(track))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.trackDao().getTracks().map { tracks ->
            tracks.map { trackDbConvertor.map(it) }
        }
    }

    override fun getFavoriteTrackIds(): Flow<List<Long>> {
        return appDatabase.trackDao().getTrackIds()
    }
}
