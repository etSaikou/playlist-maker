package com.saikou.playlistmaker.media_libr.ui.models

import com.saikou.playlistmaker.media_libr.domain.models.Playlist

sealed interface PlaylistState {
    data object Empty : PlaylistState
    data class Content(val playlists: List<Playlist>) : PlaylistState
}
