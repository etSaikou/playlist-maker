package com.saikou.playlistmaker.media_libr.ui.models

import com.saikou.playlistmaker.search.data.entity.Track

sealed interface FavoriteState {

    object Empty : FavoriteState

    data class Content(
        val tracks: List<Track>
    ) : FavoriteState
}
