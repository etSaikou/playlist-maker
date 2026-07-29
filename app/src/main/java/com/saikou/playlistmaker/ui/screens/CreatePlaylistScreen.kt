package com.saikou.playlistmaker.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.global.deserialize
import com.saikou.playlistmaker.global.showToast
import com.saikou.playlistmaker.media_libr.domain.models.Playlist
import com.saikou.playlistmaker.media_libr.ui.view_model.CreatePlaylistViewModel
import com.saikou.playlistmaker.ui.common.AppToolbar
import com.saikou.playlistmaker.ui.theme.Blue
import com.saikou.playlistmaker.ui.theme.GrayText
import com.saikou.playlistmaker.ui.theme.YsDisplay
import org.koin.androidx.compose.getViewModel

@Composable
fun CreatePlaylistScreen(
    navController: NavController,
    playlistSerialized: String? = null,
    viewModel: CreatePlaylistViewModel = getViewModel()
) {
    val context = LocalContext.current
    val initialPlaylist = remember(playlistSerialized) {
        playlistSerialized?.deserialize(Playlist::class.java)
    }
    val isEdit = initialPlaylist != null

    var name by remember(initialPlaylist) { mutableStateOf(initialPlaylist?.name ?: "") }
    var description by remember(initialPlaylist) { mutableStateOf(initialPlaylist?.description ?: "") }
    var imageUri by remember(initialPlaylist) { mutableStateOf<Uri?>(initialPlaylist?.imagePath?.let { Uri.parse(it) }) }

    var showExitDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) imageUri = uri
    }

    val onBackAction = {
        if (!isEdit && (name.isNotEmpty() || description.isNotEmpty() || imageUri != null)) {
            showExitDialog = true
        } else {
            navController.popBackStack()
        }
    }

    BackHandler {
        onBackAction()
    }

    LaunchedEffect(Unit) {
        viewModel.playlistCreatedEvent.collect { playlistName ->
            val message = if (isEdit) {
                // Technically the VM event is same, but we can differentiate here if needed
                // Using generic "Playlist updated" string if it exists, or just stick to one
                context.getString(R.string.playlist_created_msg, playlistName)
            } else {
                context.getString(R.string.playlist_created_msg, playlistName)
            }
            showToast(context, message)
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            AppToolbar(
                title = if (isEdit) stringResource(id = R.string.playlist_edit) else stringResource(id = R.string.playlist_create_new),
                onBackClick = { onBackAction() }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val strokeColor = GrayText
            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .size(312.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .then(
                        if (!isEdit && imageUri == null) {
                            Modifier
                                .background(Color.Transparent)
                                .drawBehind {
                                    drawRoundRect(
                                        color = strokeColor,
                                        style = Stroke(
                                            width = 2f,
                                            pathEffect = PathEffect.dashPathEffect(
                                                floatArrayOf(30f, 30f),
                                                0f
                                            )
                                        ),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                                            8.dp.toPx()
                                        )
                                    )
                                }
                        } else {
                            Modifier
                        }
                    )
                    .clickable { photoPickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (isEdit) {
                    AsyncImage(
                        model = R.drawable.ic_placeholder_235,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = R.drawable.ic_add_album_picture_100,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.playlist_name_hint),
                        fontFamily = YsDisplay
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Blue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    focusedLabelColor = Blue,
                    unfocusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    cursorColor = Blue
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.playlist_description_hint),
                        fontFamily = YsDisplay
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Blue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    focusedLabelColor = Blue,
                    unfocusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    cursorColor = Blue
                )
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (isEdit) {
                        viewModel.updatePlaylist(initialPlaylist!!, name, description, imageUri)
                        navController.popBackStack()
                    } else {
                        viewModel.createPlaylist(name, description, imageUri)
                    }
                },
                enabled = name.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue,
                    disabledContainerColor = GrayText
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isEdit) stringResource(id = R.string.playlist_save) else stringResource(
                        id = R.string.playlist_create_btn
                    ),
                    fontFamily = YsDisplay,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
        
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text(text = stringResource(id = R.string.playlist_create_dialog_title), fontFamily = YsDisplay) },
                text = { Text(text = stringResource(id = R.string.playlist_create_dialog_msg), fontFamily = YsDisplay) },
                confirmButton = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text(text = stringResource(id = R.string.playlist_create_dialog_confirm), color = Blue, fontFamily = YsDisplay)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) {
                        Text(text = stringResource(id = R.string.playlist_create_dialog_cancel), color = Blue, fontFamily = YsDisplay)
                    }
                },
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                textContentColor = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
