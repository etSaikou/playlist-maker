package com.saikou.playlistmaker.settings.data.impl

import android.content.Context
import androidx.core.content.edit
import com.saikou.playlistmaker.settings.domain.SettingsRepository
import com.saikou.playlistmaker.global.Const

class SettingsRepositoryImpl(private val context: Context): SettingsRepository {
    private val sharedPreferences = context.getSharedPreferences(
        Const.SHARED_PREFS,
        Context.MODE_PRIVATE
    )

    override fun getIsDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(Const.DARK_THEME_KEY, false)
    }

    override fun setIsDarkTheme(b: Boolean) {
        sharedPreferences.edit { putBoolean(Const.DARK_THEME_KEY, b) }
    }
}