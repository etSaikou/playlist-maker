package com.saikou.playlistmaker.media_libr.domain.api

import com.saikou.playlistmaker.media_libr.domain.models.Playlist
import com.saikou.playlistmaker.search.data.entity.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Flow<Boolean>
}
