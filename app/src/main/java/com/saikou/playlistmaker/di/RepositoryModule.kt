package com.saikou.playlistmaker.di

import com.saikou.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.saikou.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.saikou.playlistmaker.search.domain.HistoryRepository
import com.saikou.playlistmaker.search.domain.TrackRepository
import com.saikou.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.saikou.playlistmaker.settings.domain.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<TrackRepository> {
        TrackRepositoryImpl(get())
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
}