package com.saikou.playlistmaker.entity

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long ,
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String
)
