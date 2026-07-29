package com.saikou.playlistmaker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.saikou.playlistmaker.ui.common.AppToolbar
import com.saikou.playlistmaker.ui.navigation.Screen
import com.saikou.playlistmaker.ui.theme.Blue
import com.saikou.playlistmaker.ui.theme.GrayText
import com.saikou.playlistmaker.ui.theme.White
import org.koin.androidx.compose.getViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = getViewModel()
) {
    val isDarkTheme by viewModel.themeLiveData.observeAsState(false)

    Scaffold(
        topBar = { AppToolbar(title = stringResource(id = R.string.btn_settings)) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Theme Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.settings_dark_theme),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { viewModel.setTheme(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = White,
                        checkedTrackColor = Blue,
                        uncheckedThumbColor = White,
                        uncheckedTrackColor = GrayText,
                        checkedBorderColor = Color.Transparent,
                        uncheckedBorderColor = Color.Transparent
                    )
                )
            }

            // Share Button
            SettingsItem(
                text = stringResource(id = R.string.settings_share),
                iconRes = R.drawable.ic_share_24,
                onClick = { viewModel.shareApp() }
            )

            // Support Button
            SettingsItem(
                text = stringResource(id = R.string.settings_support),
                iconRes = R.drawable.ic_support_24,
                onClick = { viewModel.openSupport() },
            )

            // User Agreement Button
            SettingsItem(
                text = stringResource(id = R.string.settings_agreement),
                iconRes = R.drawable.ic_arrow_forward_24,
                onClick = {
                    navController.navigate(Screen.Terms.createRoute(viewModel.getLinkTerms()))
                }
            )
        }
    }
}

@Composable
private fun SettingsItem(
    text: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 21.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary
        )
    }
}
