package com.saikou.playlistmaker.settings.domain

interface SettingsRepository {
    fun getIsDarkTheme(): Boolean
    fun setIsDarkTheme(b: Boolean)
}