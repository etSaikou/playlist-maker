package com.saikou.playlistmaker.settings.domain.impl

import com.saikou.playlistmaker.settings.domain.SettingsInteractor
import com.saikou.playlistmaker.settings.domain.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository ): SettingsInteractor {
    override fun getIsDarkTheme(): Boolean {
        return settingsRepository.getIsDarkTheme()
    }

    override fun setIsDarkTheme(b: Boolean) {
        settingsRepository.setIsDarkTheme(b)
    }
}