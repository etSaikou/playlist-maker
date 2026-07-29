package com.saikou.playlistmaker.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.ui.theme.Blue
import com.saikou.playlistmaker.ui.theme.GrayText
import com.saikou.playlistmaker.ui.theme.YsDisplay

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Search,
        NavigationItem.MediaLibrary,
        NavigationItem.Settings
    )

    NavigationBar(
        containerColor = Color.Transparent // Handled by Scaffold background
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = stringResource(id = item.title)) },
                label = { Text(text = stringResource(id = item.title), fontFamily = YsDisplay) },
                selected = currentRoute == item.screen.route,
                onClick = {
                    if (currentRoute != item.screen.route) {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Blue,
                    selectedTextColor = Blue,
                    unselectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

sealed class NavigationItem(val screen: Screen, val icon: Int, val title: Int) {
    object Search : NavigationItem(Screen.Search, R.drawable.ic_search_19, R.string.btn_search)
    object MediaLibrary : NavigationItem(Screen.MediaLibrary, R.drawable.ic_medialib_20, R.string.btn_mlib)
    object Settings : NavigationItem(Screen.Settings, R.drawable.ic_settings_20, R.string.btn_settings)
}
