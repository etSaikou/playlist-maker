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

    override fun getLink(): String {
        return context.getString(R.string.agreement_link)
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

    override fun shareText(text: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(shareIntent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}