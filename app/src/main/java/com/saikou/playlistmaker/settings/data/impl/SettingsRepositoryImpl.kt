package com.saikou.playlistmaker.settings.data.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.saikou.playlistmaker.global.Const
import com.saikou.playlistmaker.settings.domain.SettingsRepository

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences): SettingsRepository {

    override fun getIsDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(Const.DARK_THEME_KEY, false)
    }

    override fun setIsDarkTheme(b: Boolean) {
        sharedPreferences.edit { putBoolean(Const.DARK_THEME_KEY, b) }
    }
}