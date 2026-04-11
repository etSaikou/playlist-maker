package com.saikou.playlistmaker.di

import com.saikou.playlistmaker.search.domain.TrackHistoryInteractor
import com.saikou.playlistmaker.search.domain.TrackInteractor
import com.saikou.playlistmaker.search.domain.impl.TrackHistoryInteractorImpl
import com.saikou.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.saikou.playlistmaker.settings.domain.SettingsInteractor
import com.saikou.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.saikou.playlistmaker.sharing.domain.SharingInteractor
import com.saikou.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<TrackInteractor> {
        TrackInteractorImpl(get())
    }

    single<TrackHistoryInteractor> {
        TrackHistoryInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }

}