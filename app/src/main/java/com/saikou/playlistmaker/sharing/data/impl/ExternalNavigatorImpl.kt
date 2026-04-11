package com.saikou.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.sharing.domain.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context): ExternalNavigator {

    override fun shareLink() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.android_dev_link))
        shareIntent.setType("text/plain")
        context.startActivity(shareIntent)
    }

    override fun openLink() {
        context.startActivity(Intent(Intent.ACTION_VIEW, context.getString(R.string.agreement_link).toUri()).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
    }

    override fun openEmail() {
        val supIntent = Intent(Intent.ACTION_SENDTO).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
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