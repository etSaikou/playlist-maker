package com.saikou.playlistmaker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.global.serialize
import com.saikou.playlistmaker.media_libr.domain.models.Playlist
import com.saikou.playlistmaker.media_libr.ui.models.FavoriteState
import com.saikou.playlistmaker.media_libr.ui.models.PlaylistState
import com.saikou.playlistmaker.media_libr.ui.view_model.FavoriteViewModel
import com.saikou.playlistmaker.media_libr.ui.view_model.PlaylistViewModel
import com.saikou.playlistmaker.ui.common.AppToolbar
import com.saikou.playlistmaker.ui.common.Placeholder
import com.saikou.playlistmaker.ui.common.TrackItem
import com.saikou.playlistmaker.ui.navigation.Screen
import com.saikou.playlistmaker.ui.theme.GrayText
import com.saikou.playlistmaker.ui.theme.YsDisplay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaLibraryScreen(
    navController: NavController,
    favoriteViewModel: FavoriteViewModel = getViewModel(),
    playlistViewModel: PlaylistViewModel = getViewModel()
) {
    val tabs = listOf(
        stringResource(id = R.string.favorite_tab_title),
        stringResource(id = R.string.playlist_tab_title)
    )
    var isDark by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState { tabs.size }
    val coroutineScope = rememberCoroutineScope()
    isDark = isSystemInDarkTheme()

    Scaffold(
        topBar = { AppToolbar(title = stringResource(id = R.string.medialib_toolbar_title)) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.padding(horizontal = 16.dp),
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 14.sp,
                                    fontFamily = YsDisplay,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = GrayText
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> FavoritesTab(navController, favoriteViewModel)
                    1 -> PlaylistsTab(navController, playlistViewModel)
                }
            }
        }
    }
    isDark = isSystemInDarkTheme()
}

@Composable
private fun FavoritesTab(navController: NavController, viewModel: FavoriteViewModel) {
    val state by viewModel.observeState().observeAsState(FavoriteState.Empty)

    when (state) {
        is FavoriteState.Content -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items((state as FavoriteState.Content).tracks) { track ->
                    TrackItem(track = track, onClick = {
                        viewModel.addToHistory(it)
                        navController.navigate(Screen.Player.createRoute(it.serialize() ?: ""))
                    })
                }
            }
        }

        is FavoriteState.Empty -> {

            Placeholder(
                imageRes = R.drawable.ic_error_not_found_light_120,
                imageResAlt =  R.drawable.ic_error_not_found_dark_120,
                text = stringResource(id = R.string.favorite_placeholder),

            )
        }
    }
}

@Composable
private fun PlaylistsTab(navController: NavController, viewModel: PlaylistViewModel) {
    val state by viewModel.observeState().observeAsState(PlaylistState.Empty)

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { navController.navigate(Screen.CreatePlaylist.route) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
            ),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Text(text = stringResource(id = R.string.playlist_create_new), fontFamily = YsDisplay)
        }

        when (state) {
            is PlaylistState.Content -> {
                val playlists = (state as PlaylistState.Content).playlists
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 24.dp)
                ) {
                    items(playlists) { playlist ->
                        PlaylistGridItem(playlist) {
                            navController.navigate(Screen.PlaylistDetail.createRoute(playlist.id))
                        }
                    }
                }
            }

            is PlaylistState.Empty -> {
                Placeholder(
                    imageRes = R.drawable.ic_error_not_found_light_120,
                    imageResAlt =   R.drawable.ic_error_not_found_light_120,
                    text = stringResource(id = R.string.playlist_placeholder)
                )
            }
        }
    }
}

@Composable
private fun PlaylistGridItem(playlist: Playlist, onClick: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(4.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = playlist.imagePath,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_placeholder_235),
            error = painterResource(id = R.drawable.ic_placeholder_235)
        )
        Text(
            text = playlist.name,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp, fontFamily = YsDisplay),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = context.resources.getQuantityString(R.plurals.tracks_count, playlist.tracksCount, playlist.tracksCount),
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, fontFamily = YsDisplay),
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
