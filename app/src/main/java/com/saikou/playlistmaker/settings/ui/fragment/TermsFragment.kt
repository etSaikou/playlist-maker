package com.saikou.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import com.saikou.playlistmaker.databinding.FragmentUserAgreementBinding
import com.saikou.playlistmaker.util.BindingFragment

class TermsFragment : BindingFragment<FragmentUserAgreementBinding>() {

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserAgreementBinding {
        return FragmentUserAgreementBinding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val link = requireArguments().getString(ARGS_LINK) ?: ""

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url ?: "")
                return true
            }
        }
        //TODO dark mode
        binding.webView.loadUrl(link)
    }

    companion object {
        private const val ARGS_LINK = "link_terms"

        fun createArgs(link: String): Bundle =
            bundleOf(ARGS_LINK to link)
    }
}