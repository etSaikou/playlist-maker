package com.saikou.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.saikou.playlistmaker.global.Const
import com.saikou.playlistmaker.search.data.local.SearchHistoryStorage
import com.saikou.playlistmaker.search.data.local.SharedPreferencesSearchHistoryStorage
import com.saikou.playlistmaker.search.data.network.BackendApi
import com.saikou.playlistmaker.search.data.network.NetworkClient
import com.saikou.playlistmaker.search.data.network.RetrofitNetworkClient
import com.saikou.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.saikou.playlistmaker.sharing.domain.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<BackendApi> {
        Retrofit.Builder()
            .baseUrl(Const.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApi::class.java)
    }
    factory {
        MediaPlayer()
    }

    single {
        androidContext()
            .getSharedPreferences(Const.SHARED_PREFS, Context.MODE_PRIVATE)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(androidContext(), get())
    }

    single<SearchHistoryStorage> {
        SharedPreferencesSearchHistoryStorage(get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

}