package com.saikou.playlistmaker.sharing.domain

interface SharingInteractor {
    fun shareApp()
    fun getLinkTerms(): String
    fun openSupport()
}