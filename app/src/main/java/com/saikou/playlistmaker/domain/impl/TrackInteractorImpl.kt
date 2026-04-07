package com.saikou.playlistmaker.domain.impl

import com.saikou.playlistmaker.domain.api.TrackInteractor
import com.saikou.playlistmaker.domain.api.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository): TrackInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(
        expression: String,
        consumer: TrackInteractor.TracksConsumer
    ) {
        executor.execute {
            consumer.consume(repository.searchTracks(expression))
        }
    }
}