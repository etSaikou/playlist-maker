package com.saikou.playlistmaker.db.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.saikou.playlistmaker.db.entity.PlaylistEntity
import com.saikou.playlistmaker.media_libr.domain.models.Playlist

class PlaylistDbConvertor(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            imagePath = playlist.imagePath,
            trackIds = gson.toJson(playlist.trackIds),
            tracksCount = playlist.tracksCount
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            imagePath = playlist.imagePath,
            trackIds = gson.fromJson(playlist.trackIds, object : TypeToken<List<Long>>() {}.type),
            tracksCount = playlist.tracksCount
        )
    }
}
