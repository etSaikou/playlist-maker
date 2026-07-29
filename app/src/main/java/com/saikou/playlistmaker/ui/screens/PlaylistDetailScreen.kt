package com.saikou.playlistmaker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.global.millisFormat
import com.saikou.playlistmaker.global.serialize
import com.saikou.playlistmaker.global.showToast
import com.saikou.playlistmaker.media_libr.domain.models.Playlist
import com.saikou.playlistmaker.media_libr.ui.view_model.PlaylistDetailsViewModel
import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.ui.common.AppToolbar
import com.saikou.playlistmaker.ui.common.TrackItem
import com.saikou.playlistmaker.ui.navigation.Screen
import com.saikou.playlistmaker.ui.theme.Blue
import com.saikou.playlistmaker.ui.theme.GrayText
import com.saikou.playlistmaker.ui.theme.LightGray
import com.saikou.playlistmaker.ui.theme.YsDisplay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    navController: NavController,
    playlistId: Int
) {
    val viewModel: PlaylistDetailsViewModel = getViewModel { parametersOf(playlistId) }

    val playlist by viewModel.observePlaylist().observeAsState()
    val tracks by viewModel.observeTracks().observeAsState(emptyList())

    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    var showMenu by remember { mutableStateOf(false) }
    val menuSheetState = rememberModalBottomSheetState()
    
    var trackToDelete by remember { mutableStateOf<Track?>(null) }
    var showDeletePlaylistDialog by remember { mutableStateOf(false) }

    val sharePlaylistLogic = {
        if (tracks.isEmpty()) {
            showToast(context, context.getString(R.string.playlist_empty_share_msg))
        } else {
            val tracksCountText = context.resources.getQuantityString(R.plurals.tracks_count, tracks.size, tracks.size)
            viewModel.sharePlaylist(tracks, tracksCountText) { millis ->
                millis.millisFormat() ?: "0:00"
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppToolbar(
                title = "",
                onBackClick = { navController.popBackStack() },
                containerColor = LightGray,
                contentColor = Color.Black
            )
        },
        sheetContent = {
            TrackListSheet(
                tracks = tracks,
                onTrackClick = { track ->
                    navController.navigate(Screen.Player.createRoute(track.serialize() ?: ""))
                },
                onTrackLongClick = { track ->
                    trackToDelete = track
                }
            )
        },
        sheetPeekHeight = 200.dp,
        containerColor = LightGray,
        sheetContainerColor = MaterialTheme.colorScheme.background,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetDragHandle = {}
    ) { padding ->
        if (playlist == null) {
            // Show loading or just blank if it's being deleted
            Box(modifier = Modifier.fillMaxSize().background(LightGray))
        } else {
            val p = playlist!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightGray)
                    .padding(padding)
            ) {
                AsyncImage(
                    model = p.imagePath.takeIf { it?.isNotEmpty() == true } ?: R.drawable.ic_placeholder_235,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_placeholder_235),
                    error = painterResource(id = R.drawable.ic_placeholder_235)
                )

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = p.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = YsDisplay,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    if (p.description.isNotEmpty()) {
                        Text(
                            text = p.description,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 8.dp),
                            fontFamily = YsDisplay,
                            color = Color.Black
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val totalMillis = tracks.sumOf { it.trackTimeMillis }
                        val totalMinutes = (totalMillis / 60000).toInt()
                        Text(
                            text = context.resources.getQuantityString(R.plurals.minutes_count, totalMinutes, totalMinutes),
                            color = Color.Black,
                            fontFamily = YsDisplay
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_track_dot_3),
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            tint = Color.Black
                        )
                        Text(
                            text = context.resources.getQuantityString(R.plurals.tracks_count, p.tracksCount, p.tracksCount),
                            color = Color.Black,
                            fontFamily = YsDisplay
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .offset(x = (-12).dp) // Compensate for IconButton internal padding
                    ) {
                        IconButton(onClick = { sharePlaylistLogic() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_share_24),
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_more_24),
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        }
        
        if (showMenu) {
            ModalBottomSheet(
                onDismissRequest = { showMenu = false },
                sheetState = menuSheetState,
                containerColor = MaterialTheme.colorScheme.background,
                dragHandle = {}
            ) {
                PlaylistMenuContent(
                    playlist = playlist!!,
                    onShareClick = {
                        showMenu = false
                        sharePlaylistLogic()
                    },
                    onEditClick = {
                        showMenu = false
                        navController.navigate(Screen.EditPlaylist.createRoute(playlist!!.serialize() ?: ""))
                    },
                    onDeleteClick = {
                        showMenu = false
                        showDeletePlaylistDialog = true
                    }
                )
            }
        }

        trackToDelete?.let { track ->
            AlertDialog(
                onDismissRequest = { trackToDelete = null },
                title = { Text(text = stringResource(id = R.string.track_delete_dialog_title), fontFamily = YsDisplay) },
                text = { Text(text = stringResource(id = R.string.track_delete_dialog_msg), fontFamily = YsDisplay) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteTrack(track.trackId)
                        trackToDelete = null
                    }) {
                        Text(text = stringResource(id = R.string.yes), color = Blue, fontFamily = YsDisplay)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { trackToDelete = null }) {
                        Text(text = stringResource(id = R.string.no), color = Blue, fontFamily = YsDisplay)
                    }
                },
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                textContentColor = MaterialTheme.colorScheme.onBackground
            )
        }

        if (showDeletePlaylistDialog) {
            AlertDialog(
                onDismissRequest = { showDeletePlaylistDialog = false },
                title = { Text(text = stringResource(id = R.string.playlist_delete_dialog_title), fontFamily = YsDisplay) },
                text = { Text(text = stringResource(id = R.string.playlist_delete_dialog_msg), fontFamily = YsDisplay) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deletePlaylist()
                        showDeletePlaylistDialog = false
                        navController.popBackStack()
                    }) {
                        Text(text = stringResource(id = R.string.yes), color = Blue, fontFamily = YsDisplay)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeletePlaylistDialog = false }) {
                        Text(text = stringResource(id = R.string.no), color = Blue, fontFamily = YsDisplay)
                    }
                },
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                textContentColor = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun TrackListSheet(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onTrackLongClick: (Track) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp)
                .size(width = 50.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(GrayText)
        )
        
        if (tracks.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Text(text = stringResource(id = R.string.playlist_empty_tracks_msg), fontFamily = YsDisplay)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(top = 12.dp)) {
                items(tracks) { track ->
                    TrackItem(
                        track = track,
                        onClick = onTrackClick,
                        onLongClick = onTrackLongClick
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistMenuContent(
    playlist: Playlist,
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = playlist.imagePath.takeIf { it?.isNotEmpty() == true } ?: R.drawable.ic_placeholder_45,
                contentDescription = null,
                modifier = Modifier.size(45.dp).clip(RoundedCornerShape(2.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_placeholder_45),
                error = painterResource(id = R.drawable.ic_placeholder_45)
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(text = playlist.name, style = MaterialTheme.typography.bodyLarge, fontFamily = YsDisplay)
                Text(
                    text = LocalContext.current.resources.getQuantityString(R.plurals.tracks_count, playlist.tracksCount, playlist.tracksCount),
                    color = GrayText,
                    fontSize = 11.sp,
                    fontFamily = YsDisplay
                )
            }
        }
        
        Text(
            text = "Поделиться",
            modifier = Modifier.fillMaxWidth().clickable { onShareClick() }.padding(vertical = 16.dp),
            fontFamily = YsDisplay
        )
        Text(
            text = "Редактировать",
            modifier = Modifier.fillMaxWidth().clickable { onEditClick() }.padding(vertical = 16.dp),
            fontFamily = YsDisplay
        )
        Text(
            text = "Удалить плейлист",
            modifier = Modifier.fillMaxWidth().clickable { onDeleteClick() }.padding(vertical = 16.dp),
            fontFamily = YsDisplay
        )
    }
}
