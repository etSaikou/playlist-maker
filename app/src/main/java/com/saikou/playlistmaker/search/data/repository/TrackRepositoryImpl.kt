package com.saikou.playlistmaker.search.data.repository

import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.search.data.entity.TrackRequest
import com.saikou.playlistmaker.search.data.entity.TrackSearchResponse
import com.saikou.playlistmaker.search.data.network.NetworkClient
import com.saikou.playlistmaker.search.domain.TrackRepository
import com.saikou.playlistmaker.util.Resource

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error(response.resultStateMessage)
            }
            200 -> {
                Resource.Success(((response as TrackSearchResponse).results.map {
                    Track(
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.trackId,
                        it.collectionName,
                        it.releaseDate?:"",
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                }))
            }
            else ->  {
                Resource.Error(response.resultStateMessage)
            }
        }
    }

    override fun emptyMessage(): String? {
        return networkClient.emptyMessage()
    }
}