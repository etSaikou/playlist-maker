package com.saikou.playlistmaker.media_libr.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saikou.playlistmaker.media_libr.domain.api.PlaylistInteractor
import com.saikou.playlistmaker.media_libr.domain.models.Playlist
import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.sharing.domain.SharingInteractor
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val playlistId: Int,
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val playlistLiveData = MutableLiveData<Playlist>()
    fun observePlaylist(): LiveData<Playlist> = playlistLiveData

    private val tracksLiveData = MutableLiveData<List<Track>>()
    fun observeTracks(): LiveData<List<Track>> = tracksLiveData

    init {
        loadPlaylist()
    }

    private fun loadPlaylist() {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId).collect { playlist ->
                playlistLiveData.postValue(playlist)
                val tracks = playlistInteractor.getTracksByIds(playlist.trackIds)
                tracksLiveData.postValue(tracks)
            }
        }
    }

    fun deleteTrack(trackId: Long) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(trackId, playlistId)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistLiveData.value?.let {
                playlistInteractor.deletePlaylist(it)
            }
        }
    }

    fun sharePlaylist(tracks: List<Track>, tracksCountText: String, trackDurationText: (Long) -> String) {
        val playlist = playlistLiveData.value ?: return
        if (tracks.isEmpty()) return

        val shareText = buildString {
            append(playlist.name)
            append("\n")
            if (playlist.description.isNotEmpty()) {
                append(playlist.description)
                append("\n")
            }
            append(tracksCountText)
            append("\n")
            tracks.forEachIndexed { index, track ->
                append("${index + 1}. ${track.artistName} - ${track.trackName} (${trackDurationText(track.trackTimeMillis)})")
                append("\n")
            }
        }
        sharingInteractor.sharePlaylist(shareText)
    }
}
