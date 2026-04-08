package com.saikou.playlistmaker.domain.impl

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import com.saikou.playlistmaker.data.settings.SettingsPreferences
import com.saikou.playlistmaker.global.Const

class SettingsPreferencesImpl(private val context: Context): SettingsPreferences {
    private val sharedPreferences = context.getSharedPreferences(
        Const.SHARED_PREFS,
        MODE_PRIVATE
    )

    override fun getIsDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(Const.DARK_THEME_KEY, false)
    }

    override fun setIsDarkTheme(b: Boolean) {
        sharedPreferences.edit { putBoolean(Const.DARK_THEME_KEY, b) }
    }
}