package com.saikou.playlistmaker.media_libr.domain.models

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val imagePath: String?,
    val trackIds: List<Long>,
    val tracksCount: Int
)
