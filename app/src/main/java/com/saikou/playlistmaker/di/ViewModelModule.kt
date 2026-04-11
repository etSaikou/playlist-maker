package com.saikou.playlistmaker.di

import com.saikou.playlistmaker.player.ui.view_model.PlayerViewModel
import com.saikou.playlistmaker.search.ui.view_model.SearchViewModel
import com.saikou.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel{(previewUrl: String) ->
        PlayerViewModel(previewUrl, get())
    }

    viewModel{
        SettingsViewModel(get(), get())
    }

}