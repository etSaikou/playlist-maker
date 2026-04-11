package com.saikou.playlistmaker.search.data.repository

import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.search.data.entity.TrackHistoryDto
import com.saikou.playlistmaker.search.data.local.SearchHistoryStorage
import com.saikou.playlistmaker.search.domain.HistoryRepository

class HistoryRepositoryImpl(private val searHistoryStorage: SearchHistoryStorage) :
    HistoryRepository {


    override fun getTracksHistory(): List<Track> {
        return searHistoryStorage.getTracksHistory().map {track ->
            Track(
                track.trackName,
                track.artistName,
                track.trackTimeMillis,
                track.artworkUrl100,
                track.trackId,
                track.collectionName,
                track.releaseDate ?: "",
                track.primaryGenreName,
                track.country,
                track.previewUrl
            )
        }
    }

    override fun addTrack(track: Track) {
        searHistoryStorage.addTrack(
            TrackHistoryDto(
                track.trackName,
                track.artistName,
                track.trackTimeMillis,
                track.artworkUrl100,
                track.trackId,
                track.collectionName,
                track.releaseDate,
                track.primaryGenreName,
                track.country,
                track.previewUrl
            )
        )

    }

    override fun clearHistory() {
        searHistoryStorage.clearHistory()
    }

}