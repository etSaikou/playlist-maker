package com.saikou.playlistmaker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.saikou.playlistmaker.ui.navigation.AppNavGraph
import com.saikou.playlistmaker.ui.navigation.BottomNavigationBar
import com.saikou.playlistmaker.ui.navigation.Screen
import com.saikou.playlistmaker.ui.theme.LightGray
import com.saikou.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun PlaylistMakerApp(isDarkTheme: Boolean) {
    PlaylistMakerTheme(darkTheme = isDarkTheme) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val showBottomBar = currentRoute in listOf(
            Screen.Search.route,
            Screen.MediaLibrary.route,
            Screen.Settings.route
        )

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    Column {
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = LightGray
                        )
                        BottomNavigationBar(navController = navController)
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                AppNavGraph(navController = navController)
            }
        }
    }
}
