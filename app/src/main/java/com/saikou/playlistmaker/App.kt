package com.saikou.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.saikou.playlistmaker.global.Const

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences(Const.SHARED_PREFS, MODE_PRIVATE)
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
