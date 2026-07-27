package com.saikou.playlistmaker.media_libr.data.repository

import com.saikou.playlistmaker.db.converter.PlaylistDbConvertor
import com.saikou.playlistmaker.db.converter.TrackDbConvertor
import com.saikou.playlistmaker.db.dao.PlaylistDao
import com.saikou.playlistmaker.db.dao.PlaylistTrackDao
import com.saikou.playlistmaker.media_libr.domain.api.PlaylistRepository
import com.saikou.playlistmaker.media_libr.domain.models.Playlist
import com.saikou.playlistmaker.search.data.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
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
        }.distinctUntilChanged()
    }

    override suspend fun saveTrack(track: Track) {
        playlistTrackDao.insertTrack(trackDbConvertor.mapToPlaylistTrack(track))
    }

    override fun getPlaylistById(id: Int): Flow<Playlist> {
        return playlistDao.getPlaylistByIdFlow(id).map { entity ->
            playlistDbConvertor.map(entity)
        }
    }

    override suspend fun getTracksByIds(trackIds: List<Long>): List<Track> {
        val tracks = playlistTrackDao.getTracksByIds(trackIds).map {
            trackDbConvertor.map(it)
        }
        val trackMap = tracks.associateBy { it.trackId }
        return trackIds.mapNotNull { trackMap[it] }
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Int) {
        val playlist = playlistDbConvertor.map(playlistDao.getPlaylistById(playlistId))
        val updatedTrackIds = playlist.trackIds.toMutableList().apply {
            remove(trackId)
        }
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            tracksCount = updatedTrackIds.size
        )
        playlistDao.updatePlaylist(playlistDbConvertor.map(updatedPlaylist))
        deleteTrackIfUnused(trackId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlist.id)
        playlist.trackIds.forEach { trackId ->
            deleteTrackIfUnused(trackId)
        }
    }

    private suspend fun deleteTrackIfUnused(trackId: Long) {
        val allPlaylists = playlistDao.getAllPlaylists().map { playlistDbConvertor.map(it) }
        val isUsed = allPlaylists.any { it.trackIds.contains(trackId) }
        if (!isUsed) {
            playlistTrackDao.deleteTrack(trackId)
        }
    }
}
