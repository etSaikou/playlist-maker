package com.saikou.playlistmaker.data.track.repository

import com.saikou.playlistmaker.data.track.entity.Track
import com.saikou.playlistmaker.data.track.entity.TrackRequest
import com.saikou.playlistmaker.data.track.entity.TrackSearchResponse
import com.saikou.playlistmaker.data.track.network.NetworkClient
import com.saikou.playlistmaker.domain.api.TrackRepository

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override fun searchTracks(expression: String): List<Track>? {
        val response = networkClient.doRequest(TrackRequest(expression))
        if (response.resultCode == 200) {
            return (response as TrackSearchResponse).results.map {
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
            }

        } else {
            return null
        }
    }
}