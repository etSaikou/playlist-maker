package com.saikou.playlistmaker.main.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.saikou.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.saikou.playlistmaker.ui.PlaylistMakerApp
import com.saikou.playlistmaker.util.ThemeState
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by settingsViewModel.themeLiveData.observeAsState(false)
            PlaylistMakerApp(isDarkTheme = isDarkTheme)
            ThemeState.updateTheme(isDarkTheme)
        }
    }
}
