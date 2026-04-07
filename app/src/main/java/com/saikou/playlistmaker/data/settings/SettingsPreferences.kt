package com.saikou.playlistmaker.data.settings

interface SettingsPreferences {
    fun getIsDarkTheme(): Boolean
    fun setIsDarkTheme(b: Boolean)
}