package com.saikou.playlistmaker.settings.domain

interface SettingsInteractor {
    fun getIsDarkTheme(): Boolean
    fun setIsDarkTheme(b: Boolean)
}