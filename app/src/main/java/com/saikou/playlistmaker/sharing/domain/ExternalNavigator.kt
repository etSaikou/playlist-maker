package com.saikou.playlistmaker.sharing.domain

interface ExternalNavigator {
    fun shareLink()
    fun getLink(): String
    fun openEmail()
}