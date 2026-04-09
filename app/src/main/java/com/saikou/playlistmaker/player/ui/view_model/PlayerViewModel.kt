package com.saikou.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.saikou.playlistmaker.search.data.entity.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val track: Track) : ViewModel() {
    private val trackLiveData = MutableLiveData(track)

    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)


    private val progressTimeLiveData = MutableLiveData("00:00")


    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = Runnable {
        if (playerStateLiveData.value == STATE_PLAYING) {
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

    fun observePlayerState(): LiveData<Int> = playerStateLiveData
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(trackLiveData.value?.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.postValue(STATE_PREPARED)
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(STATE_PLAYING)
        startTimerUpdate()
    }

    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        playerStateLiveData.postValue(STATE_PAUSED)
    }

    fun onPause() {
        pausePlayer()
    }

    private fun startTimerUpdate() {
        progressTimeLiveData.postValue(
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(
                mediaPlayer.currentPosition
            )
        )
        handler.postDelayed(timerRunnable, 200)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        progressTimeLiveData.postValue("00:00")
    }

    fun observeTrack(): LiveData<Track> = trackLiveData


    companion object {

        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3

        fun getFactory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(track)
            }
        }
    }
}