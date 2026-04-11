package com.saikou.playlistmaker.creator

import android.content.Context
import com.saikou.playlistmaker.search.data.network.RetrofitNetworkClient
import com.saikou.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.saikou.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.saikou.playlistmaker.settings.domain.SettingsRepository
import com.saikou.playlistmaker.search.domain.HistoryRepository
import com.saikou.playlistmaker.search.domain.TrackHistoryInteractor
import com.saikou.playlistmaker.search.domain.TrackInteractor
import com.saikou.playlistmaker.search.domain.TrackRepository
import com.saikou.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.saikou.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.saikou.playlistmaker.search.domain.impl.TrackHistoryInteractorImpl
import com.saikou.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.saikou.playlistmaker.settings.domain.SettingsInteractor
import com.saikou.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.saikou.playlistmaker.sharing.domain.ExternalNavigator
import com.saikou.playlistmaker.sharing.domain.SharingInteractor
import com.saikou.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {
    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context))
    }

    private fun getHistoryRepository(context: Context): HistoryRepository {

        return HistoryRepositoryImpl(context)
    }

    fun provideTrackHistoryInteractor(context: Context): TrackHistoryInteractor {
        return TrackHistoryInteractorImpl(getHistoryRepository(context))
    }

    private fun getSettings(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun provideSettings(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettings(context))
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(context))
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

}