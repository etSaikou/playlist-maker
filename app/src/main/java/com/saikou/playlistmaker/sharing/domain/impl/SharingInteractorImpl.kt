package com.saikou.playlistmaker.sharing.domain.impl

import android.content.res.Resources
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.sharing.domain.ExternalNavigator
import com.saikou.playlistmaker.sharing.domain.SharingInteractor
import com.saikou.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator): SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {

        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
       return Resources.getSystem().getString(R.string.android_dev_link)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = Resources.getSystem().getString(R.string.my_email),
            subject = Resources.getSystem().getString(R.string.support_subject_placeholder),
            message = Resources.getSystem().getString(R.string.support_msg_placeholder)
        )
    }

    private fun getTermsLink(): String {
        return Resources.getSystem().getString(R.string.agreement_link)
    }
}