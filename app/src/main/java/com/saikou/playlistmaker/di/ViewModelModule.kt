package com.saikou.playlistmaker.di

import com.saikou.playlistmaker.media_libr.ui.view_model.CreatePlaylistViewModel
import com.saikou.playlistmaker.media_libr.ui.view_model.FavoriteViewModel
import com.saikou.playlistmaker.media_libr.ui.view_model.PlaylistDetailsViewModel
import com.saikou.playlistmaker.media_libr.ui.view_model.PlaylistViewModel
import com.saikou.playlistmaker.player.ui.view_model.PlayerViewModel
import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.search.ui.view_model.SearchViewModel
import com.saikou.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel { (track: Track) ->
        PlayerViewModel(track, get(), get())
    }

    viewModel{
        SettingsViewModel(get(), get())
    }

    viewModel{
        PlaylistViewModel(get())
    }
    viewModel{
        FavoriteViewModel(get(), get())
    }

    viewModel {
        CreatePlaylistViewModel(get(), get())
    }

    viewModel { (playlistId: Int) ->
        PlaylistDetailsViewModel(playlistId, get(), get())
    }

}