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
                add(track.trackId)
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
}
