package com.saikou.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saikou.playlistmaker.media_libr.domain.FavoriteInteractor
import com.saikou.playlistmaker.media_libr.domain.api.PlaylistInteractor
import com.saikou.playlistmaker.media_libr.domain.models.Playlist
import com.saikou.playlistmaker.player.data.PlayerState
import com.saikou.playlistmaker.player.data.PlayerStateEnum
import com.saikou.playlistmaker.search.data.entity.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class PlayerViewModel(
    private val track: Track,
    private val mediaPlayer: MediaPlayer,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playerStateLiveData =
        MutableLiveData<PlayerState>(PlayerState(PlayerStateEnum.STATE_DEFAULT, "00:00"))

    private val isFavoriteLiveData = MutableLiveData<Boolean>(track.isFavorite)

    private val playlistsLiveData = MutableLiveData<List<Playlist>>()

    private val addTrackStatusLiveData = MutableLiveData<Pair<String, Boolean>>()

    private var timerJob: Job? = null

    init {
        preparePlayer()
        checkFavoriteStatus()
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect {
                playlistsLiveData.postValue(it)
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addTrackToPlaylist(playlist, track).collect { added ->
                addTrackStatusLiveData.postValue(Pair(playlist.name, added))
                if (added) {
                    loadPlaylists() // Refresh playlists to update track count
                }
            }
        }
    }

    fun observePlaylists(): LiveData<List<Playlist>> = playlistsLiveData

    fun observeAddTrackStatus(): LiveData<Pair<String, Boolean>> = addTrackStatusLiveData

    private fun checkFavoriteStatus() {
        viewModelScope.launch {
            favoriteInteractor.getFavoriteTrackIds().collect { favoriteIds ->
                isFavoriteLiveData.postValue(favoriteIds.contains(track.trackId))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    fun observeIsFavorite(): LiveData<Boolean> = isFavoriteLiveData

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value?.state) {
            PlayerStateEnum.STATE_PLAYING -> pausePlayer()
            PlayerStateEnum.STATE_PREPARED, PlayerStateEnum.STATE_PAUSED -> startPlayer()
            PlayerStateEnum.STATE_DEFAULT, null -> {}

        }
    }

    fun onFavoriteButtonClicked() {
        viewModelScope.launch {
            if (isFavoriteLiveData.value == true) {
                favoriteInteractor.deleteTrack(track)
                isFavoriteLiveData.postValue(false)
            } else {
                favoriteInteractor.addTrack(track)
                isFavoriteLiveData.postValue(true)
            }
        }
    }

    private fun updateState(state: PlayerStateEnum, timer: String) {
        playerStateLiveData.postValue(
            playerStateLiveData.value.apply {
                this?.state = state

                this?.timer = timer
            })
    }

    private fun preparePlayer() {
        if (track.previewUrl.isNullOrEmpty()) {
            updateState(PlayerStateEnum.STATE_DEFAULT, "00:00")
            return
        }
        mediaPlayer.setDataSource(track.previewUrl)

        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            updateState(PlayerStateEnum.STATE_PREPARED, "00:00")
        }

        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            updateState(PlayerStateEnum.STATE_PREPARED,"00:00")
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        updateState(PlayerStateEnum.STATE_PLAYING, getCurrentPlayerPosition())
        startTimerUpdate()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        updateState(PlayerStateEnum.STATE_PAUSED,getCurrentPlayerPosition())
    }

    private fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
        updateState(PlayerStateEnum.STATE_DEFAULT,"00:00")
    }

    fun onPause() {
        pausePlayer()
    }

    private fun startTimerUpdate() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(300L.milliseconds)
                updateState(PlayerStateEnum.STATE_PLAYING,getCurrentPlayerPosition())
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition) ?: "00:00"
    }
}
