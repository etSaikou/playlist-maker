package com.saikou.playlistmaker.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.ui.common.AppToolbar

@Composable
fun TermsScreen(
    navController: NavController,
    link: String
) {
    Scaffold(
        topBar = {
            AppToolbar(
                title = stringResource(id = R.string.terms_title),
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    loadUrl(link)
                }
            },
            modifier = Modifier.padding(padding)
        )
    }
}
