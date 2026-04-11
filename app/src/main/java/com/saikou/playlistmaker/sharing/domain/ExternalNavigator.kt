package com.saikou.playlistmaker.sharing.domain

import com.saikou.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink()
    fun openLink()
    fun openEmail()
}