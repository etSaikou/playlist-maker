package com.saikou.playlistmaker.media_libr.data.repository

import com.saikou.playlistmaker.db.converter.PlaylistDbConvertor
import com.saikou.playlistmaker.db.converter.TrackDbConvertor
import com.saikou.playlistmaker.db.dao.PlaylistDao
import com.saikou.playlistmaker.db.dao.PlaylistTrackDao
import com.saikou.playlistmaker.media_libr.domain.api.PlaylistRepository
import com.saikou.playlistmaker.media_libr.domain.models.Playlist
import com.saikou.playlistmaker.search.data.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val trackDbConvertor: TrackDbConvertor,
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(playlistDbConvertor.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlistDbConvertor.map(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists().map { entities ->
            entities.map { playlistDbConvertor.map(it) }
        }
    }

    override suspend fun saveTrack(track: Track) {
        playlistTrackDao.insertTrack(trackDbConvertor.mapToPlaylistTrack(track))
    }
}
