package com.saikou.playlistmaker.sharing.domain

import com.saikou.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(shareLink: String)
    fun openLink(termsLink: String)
    fun openEmail(email: EmailData)
}