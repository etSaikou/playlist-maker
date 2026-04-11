package com.saikou.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.saikou.playlistmaker.player.data.PlayerState
import com.saikou.playlistmaker.player.data.PlayerStateEnum
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val previewUrl: String, private val mediaPlayer: MediaPlayer) :
    ViewModel() {


    private val playerStateLiveData =
        MutableLiveData<PlayerState>(PlayerState(PlayerStateEnum.STATE_DEFAULT, "00:00"))


    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = Runnable {
        if (playerStateLiveData.value?.state == PlayerStateEnum.STATE_PLAYING) {
            startTimerUpdate()
        }
    }

    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        resetTimer()
    }

    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData


    fun onPlayButtonClicked() {
        when (playerStateLiveData.value?.state) {
            PlayerStateEnum.STATE_PLAYING -> pausePlayer()
            PlayerStateEnum.STATE_PREPARED, PlayerStateEnum.STATE_PAUSED -> startPlayer()
            PlayerStateEnum.STATE_DEFAULT, null -> {}

        }
    }

    private fun updateState(state: PlayerStateEnum) {
        playerStateLiveData.postValue(playerStateLiveData.value.apply {
            this?.state = state
        })

    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)

        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            updateState(PlayerStateEnum.STATE_PREPARED)
        }

        mediaPlayer.setOnCompletionListener {
            updateState(PlayerStateEnum.STATE_PREPARED)
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        updateState(PlayerStateEnum.STATE_PLAYING)
        startTimerUpdate()
    }

    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        updateState(PlayerStateEnum.STATE_PAUSED)
    }

    fun onPause() {
        pausePlayer()
    }

    private fun startTimerUpdate() {
        playerStateLiveData.postValue(playerStateLiveData.value.apply {
            this?.timer = SimpleDateFormat("mm:ss", Locale.getDefault()).format(
                mediaPlayer.currentPosition
            )
        })

        handler.postDelayed(timerRunnable, 200)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        playerStateLiveData
        playerStateLiveData.postValue(playerStateLiveData.value.apply {
            this?.timer = "00:00"
        })
    }
}