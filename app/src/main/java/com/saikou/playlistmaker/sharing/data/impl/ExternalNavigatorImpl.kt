package com.saikou.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.core.net.toUri
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.sharing.domain.ExternalNavigator
import com.saikou.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(private val context: Context): ExternalNavigator {

    override fun shareLink() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.android_dev_link))
        shareIntent.setType("text/plain")
        context.startActivity(shareIntent)
    }

    override fun openLink() {
        context.startActivity(Intent(Intent.ACTION_VIEW, context.getString(R.string.agreement_link).toUri()))
    }

    override fun openEmail() {
        val supIntent = Intent(Intent.ACTION_SENDTO)
        supIntent.data = "mailto:".toUri()
        supIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.my_email)))
        supIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            context.getString(R.string.support_subject_placeholder)
        )
        supIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_msg_placeholder))
        context.startActivity(supIntent)
    }
}