package com.saikou.playlistmaker

import android.content.Context
import com.saikou.playlistmaker.data.settings.SettingsPreferences
import com.saikou.playlistmaker.data.track.network.RetrofitNetworkClient
import com.saikou.playlistmaker.data.track.repository.HistoryRepositoryImpl
import com.saikou.playlistmaker.data.track.repository.TrackRepositoryImpl
import com.saikou.playlistmaker.domain.api.HistoryRepository
import com.saikou.playlistmaker.domain.api.TrackHistoryInteractor
import com.saikou.playlistmaker.domain.api.TrackInteractor
import com.saikou.playlistmaker.domain.api.TrackRepository
import com.saikou.playlistmaker.domain.impl.SettingsPreferencesImpl
import com.saikou.playlistmaker.domain.impl.TrackHistoryInteractorImpl
import com.saikou.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun getHistoryRepository(context: Context): HistoryRepository {

        return HistoryRepositoryImpl(context)
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    fun provideTrackHistoryInteractor(context: Context): TrackHistoryInteractor {
        return TrackHistoryInteractorImpl(getHistoryRepository(context))
    }

    fun getSettings(context: Context): SettingsPreferences {
        return SettingsPreferencesImpl(context)
    }

}