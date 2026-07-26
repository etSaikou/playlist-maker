package com.saikou.playlistmaker.search.domain.impl

import com.saikou.playlistmaker.media_libr.domain.FavoriteInteractor
import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.search.domain.TrackInteractor
import com.saikou.playlistmaker.search.domain.TrackRepository
import com.saikou.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.concurrent.Executors

class TrackInteractorImpl(
    private val repository: TrackRepository,
    private val favoriteInteractor: FavoriteInteractor,
) : TrackInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, List<String?>?>> {
        return repository.searchTracks(expression).combine(favoriteInteractor.getFavoriteTrackIds()) { result, favoriteIds ->
            when (result) {
                is Resource.Success -> {
                    val enrichedTracks = result.data?.map { track ->
                        track.copy(isFavorite = favoriteIds.contains(track.trackId))
                    }
                    Pair(enrichedTracks, null)
                }

                is Resource.Error -> {
                    Pair(null, listOf(result.message, result.additionalMessage))
                }
            }
        }
    }

    override fun sendEmptyMessage(): String? {
        return repository.emptyMessage()
    }
}