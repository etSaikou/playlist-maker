package com.saikou.playlistmaker.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Search : Screen("search")
    object MediaLibrary : Screen("media_library")
    object Settings : Screen("settings")
    object Player : Screen("player/{track_info}") {
        fun createRoute(trackInfo: String) = "player/${Uri.encode(trackInfo)}"
    }
    object PlaylistDetail : Screen("playlist_detail/{playlist_id}") {
        fun createRoute(playlistId: Int) = "playlist_detail/$playlistId"
    }
    object CreatePlaylist : Screen("create_playlist")
    object EditPlaylist : Screen("edit_playlist/{playlist}") {
        fun createRoute(playlist: String) = "edit_playlist/${Uri.encode(playlist)}"
    }
    object Terms : Screen("terms/{link}") {
        fun createRoute(link: String) = "terms/${Uri.encode(link)}"
    }
}
