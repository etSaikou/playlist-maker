package com.saikou.playlistmaker.sharing.domain.impl

import com.saikou.playlistmaker.sharing.domain.ExternalNavigator
import com.saikou.playlistmaker.sharing.domain.SharingInteractor

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink()
    }

    override fun getLinkTerms(): String {
        return externalNavigator.getLink()
    }

    override fun openSupport() {

        externalNavigator.openEmail()
    }
}