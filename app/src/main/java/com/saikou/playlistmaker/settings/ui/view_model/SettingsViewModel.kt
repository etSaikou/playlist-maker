package com.saikou.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.saikou.playlistmaker.settings.domain.SettingsInteractor
import com.saikou.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val settingsData = MutableLiveData<Boolean>()

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun setTheme(isDarkTheme: Boolean) {
        settingsData.postValue(isDarkTheme)
    }

    fun observeTheme(): LiveData<Boolean> {
        return settingsData
    }

    companion object {
        fun getFactory(
            sharingInteractor: SharingInteractor,
            settingsInteractor: SettingsInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(sharingInteractor, settingsInteractor)
            }
        }
    }

}