package com.saikou.playlistmaker.search.data.repository

import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.search.data.entity.TrackRequest
import com.saikou.playlistmaker.search.data.entity.TrackSearchResponse
import com.saikou.playlistmaker.search.data.network.NetworkClient
import com.saikou.playlistmaker.search.domain.TrackRepository
import com.saikou.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(response.resultStateMessage, response.resultAdditionalMessage))
            }

            200 -> {
                with(response as TrackSearchResponse) {
                    val data = response.results.map {
                        Track(
                            it.trackName,
                            it.artistName,
                            it.trackTimeMillis,
                            it.artworkUrl100,
                            it.trackId,
                            it.collectionName,
                            it.releaseDate ?: "",
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl
                        )
                    }
                    emit(Resource.Success(data))
                }

            }
            else -> emit(Resource.Error(message = response.resultStateMessage, null))
        }
    }


    override fun emptyMessage(): String? {
        return networkClient.emptyMessage()
    }
}