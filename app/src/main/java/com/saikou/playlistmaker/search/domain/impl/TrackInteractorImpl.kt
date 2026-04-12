package com.saikou.playlistmaker.search.domain.impl

import android.util.Log
import com.saikou.playlistmaker.search.domain.TrackInteractor
import com.saikou.playlistmaker.search.domain.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository): TrackInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(
        expression: String,
        consumer: TrackInteractor.TracksConsumer
    ) {
        executor.execute {
            val response = repository.searchTracks(expression)
            consumer.consume(response.data,response.message, response.additionalMessage )
        }
    }

    override fun sendEmptyMessage(): String? {
        return repository.emptyMessage()
    }
}