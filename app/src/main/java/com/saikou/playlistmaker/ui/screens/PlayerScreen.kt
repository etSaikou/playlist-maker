package com.saikou.playlistmaker.ui.screens

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.global.deserialize
import com.saikou.playlistmaker.global.millisFormat
import com.saikou.playlistmaker.global.replaceDimensionArtwork
import com.saikou.playlistmaker.global.showToast
import com.saikou.playlistmaker.media_libr.domain.models.Playlist
import com.saikou.playlistmaker.player.data.PlayerStateEnum
import com.saikou.playlistmaker.player.ui.service.AudioPlayerService
import com.saikou.playlistmaker.player.ui.view_model.PlayerViewModel
import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.ui.common.AppToolbar
import com.saikou.playlistmaker.ui.navigation.Screen
import com.saikou.playlistmaker.ui.theme.Blue
import com.saikou.playlistmaker.ui.theme.GrayText
import com.saikou.playlistmaker.ui.theme.YsDisplay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    navController: NavController,
    trackInfo: String
) {
    val track = trackInfo.deserialize(Track::class.java) ?: return
    val viewModel: PlayerViewModel = getViewModel { parametersOf(track) }
    val context = LocalContext.current

    val playerState by viewModel.observePlayerState().observeAsState()
    val isFavorite by viewModel.observeIsFavorite().observeAsState(false)
    val playlists by viewModel.observePlaylists().observeAsState(emptyList())
    val addTrackStatus by viewModel.observeAddTrackStatus().observeAsState()

    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val scrimAlpha by animateFloatAsState(
        targetValue = if (scaffoldState.bottomSheetState.targetValue == SheetValue.Expanded) 0.6f else 0f,
        label = "ScrimAlpha"
    )

    LaunchedEffect(addTrackStatus) {
        addTrackStatus?.let { (playlistName, added) ->
            val message = if (added) {
                context.getString(R.string.added_to_playlist, playlistName)
            } else {
                context.getString(R.string.already_in_playlist, playlistName)
            }
            showToast(context, message)
            if (added) {
                scope.launch { scaffoldState.bottomSheetState.partialExpand() }
            }
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.hideNotification()
                Lifecycle.Event.ON_STOP -> viewModel.showNotification()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(Unit) {
        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as AudioPlayerService.AudioPlayerBinder
                viewModel.onServiceConnected(binder.getService())
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                viewModel.onServiceDisconnected()
            }
        }

        val intent = Intent(context, AudioPlayerService::class.java).apply {
            putExtra(AudioPlayerService.EXTRA_URL, track.previewUrl)
            putExtra(AudioPlayerService.EXTRA_TRACK_NAME, track.trackName)
            putExtra(AudioPlayerService.EXTRA_ARTIST_NAME, track.artistName)
        }
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        onDispose {
            viewModel.onPause()
            viewModel.hideNotification()
            context.unbindService(serviceConnection)
            viewModel.onServiceDisconnected()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = { AppToolbar(title = "", onBackClick = { navController.popBackStack() }) },
        sheetContent = {
            PlaylistBottomSheet(
                playlists = playlists,
                onNewPlaylistClick = { navController.navigate(Screen.CreatePlaylist.route) },
                onPlaylistClick = { viewModel.addTrackToPlaylist(it) }
            )
        },
        sheetPeekHeight = 0.dp,
        sheetContainerColor = MaterialTheme.colorScheme.background,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetDragHandle = {}
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = track.artworkUrl100.replaceDimensionArtwork(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(top = 26.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_placeholder_45)
                )

                Text(
                    text = track.trackName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    maxLines = 1,
                    fontFamily = YsDisplay,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    maxLines = 1,
                    fontFamily = YsDisplay
                )

                Row(
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { scope.launch { scaffoldState.bottomSheetState.expand() } }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_to_collection_light_not_toggled_51),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }

                    IconButton(
                        onClick = { viewModel.onPlayButtonClicked() },
                        modifier = Modifier
                            .weight(1f)
                            .size(100.dp)
                    ) {
                        val playIcon = if (playerState?.state == PlayerStateEnum.STATE_PLAYING) {
                            R.drawable.ic_pause_button_100
                        } else {
                            R.drawable.ic_play_button_100
                        }
                        Icon(
                            painter = painterResource(id = playIcon),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = { viewModel.onFavoriteButtonClicked() }) {
                        val favIcon = if (isFavorite) {
                            R.drawable.ic_favorite_button_light_toggled_51
                        } else {
                            R.drawable.ic_favorite_button_light_not_toggled_51
                        }

                        Image(
                            painter = painterResource(id = favIcon),
                            contentDescription = null

                        )
                    }
                }

                Text(
                    text = playerState?.timer ?: "00:00",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                    modifier = Modifier.padding(top = 4.dp),
                    fontFamily = YsDisplay
                )

                Spacer(modifier = Modifier.size(30.dp))

                TrackDetailRow(label = stringResource(id = R.string.player_duration), value = track.trackTimeMillis.millisFormat() ?: "0:00")
                if (track.collectionName.isNotEmpty()) {
                    TrackDetailRow(label = stringResource(id = R.string.player_album), value = track.collectionName)
                }
                TrackDetailRow(label = stringResource(id = R.string.player_year), value = track.releaseDate.substringBefore('-'))
                TrackDetailRow(label = stringResource(id = R.string.player_genre), value = track.primaryGenreName)
                TrackDetailRow(label = stringResource(id = R.string.player_country), value = track.country)
            }

            // Manual Scrim (inside Scaffold content, so it's behind the sheet)
            if (scrimAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(scrimAlpha)
                        .background(Color.Black)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                        }
                )
            }
        }
    }
}

@Composable
private fun TrackDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            ),
            color = GrayText,
            fontFamily = YsDisplay
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            fontFamily = YsDisplay,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun PlaylistBottomSheet(
    playlists: List<Playlist>,
    onNewPlaylistClick: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .size(width = 50.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(GrayText)
        )
        Text(
            text = stringResource(id = R.string.add_to_playlist),
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp),
            modifier = Modifier.padding(bottom = 16.dp),
            fontFamily = YsDisplay
        )
        Button(
            onClick = onNewPlaylistClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Text(text = stringResource(id = R.string.playlist_create_new), fontFamily = YsDisplay)
        }
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(playlists) { playlist ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlaylistClick(playlist) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = playlist.imagePath,
                        contentDescription = null,
                        modifier = Modifier.size(45.dp).clip(RoundedCornerShape(2.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.ic_placeholder_45)
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
            }
        }
    }
}
