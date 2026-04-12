package com.saikou.playlistmaker.sharing.domain.impl

import android.content.res.Resources
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.sharing.domain.ExternalNavigator
import com.saikou.playlistmaker.sharing.domain.SharingInteractor
import com.saikou.playlistmaker.sharing.domain.model.EmailData

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