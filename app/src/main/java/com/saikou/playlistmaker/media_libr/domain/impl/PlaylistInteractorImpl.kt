package com.saikou.playlistmaker.media_libr.domain.impl

import com.saikou.playlistmaker.media_libr.domain.api.PlaylistInteractor
import com.saikou.playlistmaker.media_libr.domain.api.PlaylistRepository
import com.saikou.playlistmaker.media_libr.domain.models.Playlist
import com.saikou.playlistmaker.search.data.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistRepository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Flow<Boolean> = flow {
        if (playlist.trackIds.contains(track.trackId)) {
            emit(false)
        } else {
            val updatedTrackIds = playlist.trackIds.toMutableList().apply {
                add(0, track.trackId)
            }
            val updatedPlaylist = playlist.copy(
                trackIds = updatedTrackIds,
                tracksCount = updatedTrackIds.size
            )
            playlistRepository.updatePlaylist(updatedPlaylist)
            playlistRepository.saveTrack(track)
            emit(true)
        }
    }

    override fun getPlaylistById(id: Int): Flow<Playlist> {
        return playlistRepository.getPlaylistById(id)
    }

    override suspend fun getTracksByIds(trackIds: List<Long>): List<Track> {
        return playlistRepository.getTracksByIds(trackIds)
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Int) {
        playlistRepository.removeTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }
}
