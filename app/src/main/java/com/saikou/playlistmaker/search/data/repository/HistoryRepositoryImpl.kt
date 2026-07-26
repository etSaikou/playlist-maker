package com.saikou.playlistmaker.search.data.repository

import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.search.data.entity.TrackHistoryDto
import com.saikou.playlistmaker.search.data.local.SearchHistoryStorage
import com.saikou.playlistmaker.search.domain.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class HistoryRepositoryImpl(private val searHistoryStorage: SearchHistoryStorage) :
    HistoryRepository {


    override fun getTracksHistory(): Flow<List<Track>> = flow {
        val historyData = searHistoryStorage.getTracksHistory().map { track ->
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
        emit(historyData)
    }


    override suspend fun addTrack(track: Track) {
        withContext(Dispatchers.IO) {
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
    }

    override fun clearHistory() {
        searHistoryStorage.clearHistory()
    }

}