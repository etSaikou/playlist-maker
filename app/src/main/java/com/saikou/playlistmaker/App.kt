package com.saikou.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.saikou.playlistmaker.di.dataModule
import com.saikou.playlistmaker.di.interactorModule
import com.saikou.playlistmaker.di.repositoryModule
import com.saikou.playlistmaker.di.viewModelModule
import com.saikou.playlistmaker.global.Const
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        val sharedPreferences: SharedPreferences by inject()

        switchTheme(sharedPreferences.getBoolean(Const.DARK_THEME_KEY,false))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
