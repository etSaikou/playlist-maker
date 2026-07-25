package com.saikou.playlistmaker.di

import com.saikou.playlistmaker.db.converter.TrackDbConvertor
import com.saikou.playlistmaker.media_libr.data.repository.FavoriteRepositoryImpl
import com.saikou.playlistmaker.media_libr.domain.FavoriteRepository
import com.saikou.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.saikou.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.saikou.playlistmaker.search.domain.HistoryRepository
import com.saikou.playlistmaker.search.domain.TrackRepository
import com.saikou.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.saikou.playlistmaker.settings.domain.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {

    factory { TrackDbConvertor() }

    single<TrackRepository> {
        TrackRepositoryImpl(get())
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }
}