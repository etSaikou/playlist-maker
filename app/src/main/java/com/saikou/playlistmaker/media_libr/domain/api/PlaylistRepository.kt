package com.saikou.playlistmaker.media_libr.domain.api

import com.saikou.playlistmaker.media_libr.domain.models.Playlist
import com.saikou.playlistmaker.search.data.entity.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun saveTrack(track: Track)
    fun getPlaylistById(id: Int): Flow<Playlist>
    suspend fun getTracksByIds(trackIds: List<Long>): List<Track>
    suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Int)
    suspend fun deletePlaylist(playlist: Playlist)
}
