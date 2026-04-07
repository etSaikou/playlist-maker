package com.saikou.playlistmaker.data.track.entity

data class TrackHistoryDto(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long ,
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
)
