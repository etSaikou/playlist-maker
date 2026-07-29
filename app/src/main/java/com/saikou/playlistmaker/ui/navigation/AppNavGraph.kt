package com.saikou.playlistmaker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.saikou.playlistmaker.ui.screens.*

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.MediaLibrary.route
    ) {
        composable(Screen.Search.route) {
            SearchScreen(navController)
        }
        composable(Screen.MediaLibrary.route) {
            MediaLibraryScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
        composable(
            route = Screen.Player.route,
            arguments = listOf(navArgument("track_info") { type = NavType.StringType })
        ) { backStackEntry ->
            val trackInfo = backStackEntry.arguments?.getString("track_info") ?: ""
            PlayerScreen(navController, trackInfo)
        }
        composable(
            route = Screen.PlaylistDetail.route,
            arguments = listOf(navArgument("playlist_id") { type = NavType.IntType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getInt("playlist_id") ?: 0
            PlaylistDetailScreen(navController, playlistId)
        }
        composable(Screen.CreatePlaylist.route) {
            CreatePlaylistScreen(navController)
        }
        composable(
            route = Screen.EditPlaylist.route,
            arguments = listOf(navArgument("playlist") { type = NavType.StringType })
        ) { backStackEntry ->
            val playlist = backStackEntry.arguments?.getString("playlist")
            CreatePlaylistScreen(navController, playlist)
        }
        composable(
            route = Screen.Terms.route,
            arguments = listOf(navArgument("link") { type = NavType.StringType })
        ) { backStackEntry ->
            val link = backStackEntry.arguments?.getString("link") ?: ""
            TermsScreen(navController, link)
        }
    }
}
