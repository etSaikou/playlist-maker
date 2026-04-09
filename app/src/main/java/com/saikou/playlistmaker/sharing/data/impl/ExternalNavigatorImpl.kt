package com.saikou.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.saikou.playlistmaker.sharing.domain.ExternalNavigator
import com.saikou.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(private val context: Context): ExternalNavigator {

    override fun shareLink(shareLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareLink)
        shareIntent.setType("text/plain")
        context.startActivity(shareIntent)
    }

    override fun openLink(termsLink: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, termsLink.toUri()))
    }

    override fun openEmail(emailData: EmailData) {
        val supIntent = Intent(Intent.ACTION_SENDTO)
        supIntent.data = "mailto:".toUri()
        supIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
        supIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            emailData.subject
        )
        supIntent.putExtra(Intent.EXTRA_TEXT, emailData.message)
        context.startActivity(supIntent)
    }
}