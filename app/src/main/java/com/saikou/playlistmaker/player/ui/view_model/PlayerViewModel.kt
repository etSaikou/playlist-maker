package com.saikou.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saikou.playlistmaker.player.data.PlayerState
import com.saikou.playlistmaker.player.data.PlayerStateEnum
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class PlayerViewModel(private val previewUrl: String, private val mediaPlayer: MediaPlayer) :
    ViewModel() {

    private val playerStateLiveData =
        MutableLiveData<PlayerState>(PlayerState(PlayerStateEnum.STATE_DEFAULT, "00:00"))

    private var timerJob: Job? = null

    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData


    fun onPlayButtonClicked() {
        when (playerStateLiveData.value?.state) {
            PlayerStateEnum.STATE_PLAYING -> pausePlayer()
            PlayerStateEnum.STATE_PREPARED, PlayerStateEnum.STATE_PAUSED -> startPlayer()
            PlayerStateEnum.STATE_DEFAULT, null -> {}

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
        mediaPlayer.setDataSource(previewUrl)

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
