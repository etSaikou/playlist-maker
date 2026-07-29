package com.saikou.playlistmaker.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.global.serialize
import com.saikou.playlistmaker.global.showToast
import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.search.data.entity.TrackState
import com.saikou.playlistmaker.search.ui.view_model.SearchViewModel
import com.saikou.playlistmaker.ui.common.AppToolbar
import com.saikou.playlistmaker.ui.common.Placeholder
import com.saikou.playlistmaker.ui.common.TrackItem
import com.saikou.playlistmaker.ui.navigation.Screen
import com.saikou.playlistmaker.ui.theme.Black
import com.saikou.playlistmaker.ui.theme.Blue
import com.saikou.playlistmaker.ui.theme.YsDisplay
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = getViewModel()
) {
    val state by viewModel.observeState().observeAsState(TrackState.Content(emptyList()))
    val toastMessage by viewModel.observeShowToast().observeAsState()
    val searchText by viewModel.searchText.observeAsState("")
    
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.clearSearch()
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            showToast(context, it)
        }
    }

    Scaffold(
        topBar = { AppToolbar(title = stringResource(id = R.string.btn_search)) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            // Search Bar
            TextField(
                value = searchText,
                onValueChange = {
                    viewModel.searchDebounce(it, false)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused && searchText.isEmpty()) {
                            viewModel.clearSearch()
                        }
                    },
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.btn_search),
                        fontSize = 16.sp,
                        fontFamily = YsDisplay,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search_19),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(19.dp)
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = {
                            viewModel.clearSearch()
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_clear_x_16),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = Blue,
                    focusedTextColor = Black,
                    unfocusedTextColor = Black,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.searchDebounce(searchText, true)
                    keyboardController?.hide()
                    focusManager.clearFocus()
                })
            )

            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                when (state) {
                    is TrackState.Loading, is TrackState.LoadingHistory -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 140.dp),
                            color = Blue
                        )
                    }

                    is TrackState.Content -> {
                        TrackList((state as TrackState.Content).tracks) { track ->
                            viewModel.addToHistory(track)
                            navController.navigate(Screen.Player.createRoute(track.serialize() ?: ""))
                        }
                    }

                    is TrackState.History -> {
                        val history = (state as TrackState.History).trackHistory
                        if (history.isNotEmpty()) {
                            HistoryList(history, onTrackClick = { track ->
                                viewModel.addToHistory(track)
                                navController.navigate(Screen.Player.createRoute(track.serialize() ?: ""))
                            }, onClearClick = {
                                viewModel.clearHistory()
                            })
                        }
                    }

                    is TrackState.Empty -> {
                        Placeholder(
                            imageRes = R.drawable.ic_error_not_found_light_120,
                            imageResAlt =  R.drawable.ic_error_not_found_dark_120,
                            text = (state as TrackState.Empty).message
                        )
                    }

                    is TrackState.Error -> {
                        Placeholder(
                            imageRes = R.drawable.ic_error_net_light_120,
                            imageResAlt =  R.drawable.ic_error_net_dark_120,

                            text = (state as TrackState.Error).message,
                            buttonText = stringResource(id = R.string.search_refresh_button),
                            onButtonClick = { viewModel.searchDebounce(searchText, true) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackList(tracks: List<Track>, onTrackClick: (Track) -> Unit) {
    LazyColumn {
        items(tracks) { track ->
            TrackItem(track = track, onClick = onTrackClick)
        }
    }
}

@Composable
private fun HistoryList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearClick: () -> Unit
) {
    Column {
        Text(
            text = stringResource(id = R.string.search_history_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 19.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = YsDisplay
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp, bottom = 12.dp)
        )
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tracks.reversed()) { track ->
                TrackItem(track = track, onClick = onTrackClick)
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onClearClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.background
                        ),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Text(text = stringResource(id = R.string.clear_history_btn), fontFamily = YsDisplay)
                    }
                }
            }
        }
    }
}
