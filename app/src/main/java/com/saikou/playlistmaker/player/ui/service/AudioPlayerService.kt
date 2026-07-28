package com.saikou.playlistmaker.player.ui.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.main.ui.MainActivity
import com.saikou.playlistmaker.player.data.PlayerState
import com.saikou.playlistmaker.player.data.PlayerStateEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class AudioPlayerService : Service(), AudioPlayerControl {

    private val mediaPlayer = MediaPlayer()
    private val binder = AudioPlayerBinder()

    private val _playerState = MutableStateFlow(PlayerState(PlayerStateEnum.STATE_DEFAULT, "00:00"))
    private val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private var timerJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main)

    private var trackName: String? = null
    private var artistName: String? = null
    private var previewUrl: String? = null

    inner class AudioPlayerBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        previewUrl = intent?.getStringExtra(EXTRA_URL)
        trackName = intent?.getStringExtra(EXTRA_TRACK_NAME)
        artistName = intent?.getStringExtra(EXTRA_ARTIST_NAME)
        
        previewUrl?.let { preparePlayer(it) }
        
        return binder
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            updateState(PlayerStateEnum.STATE_PREPARED, "00:00")
        }
        mediaPlayer.setOnCompletionListener {
            stopTimer()
            updateState(PlayerStateEnum.STATE_PREPARED, "00:00")
            hideNotification()
        }
    }

    override fun play() {
        mediaPlayer.start()
        updateState(PlayerStateEnum.STATE_PLAYING, getCurrentPosition())
        startTimer()
    }

    override fun pause() {
        mediaPlayer.pause()
        stopTimer()
        updateState(PlayerStateEnum.STATE_PAUSED, getCurrentPosition())
    }

    override fun getPlayerState(): StateFlow<PlayerState> = playerState

    private fun updateState(state: PlayerStateEnum, timer: String) {
        _playerState.value = PlayerState(state, timer)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(300.milliseconds)
                updateState(PlayerStateEnum.STATE_PLAYING, getCurrentPosition())
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    private fun getCurrentPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition) ?: "00:00"
    }

    override fun showNotification() {
        if (_playerState.value.state == PlayerStateEnum.STATE_PLAYING) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (!notificationManager.areNotificationsEnabled()) return

            try {
                val notification = createNotification()
                ServiceCompat.startForeground(
                    this,
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("$artistName - $trackName")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        val name = "Audio Player Channel"
        val descriptionText = "Channel for audio player notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        mediaPlayer.release()
        stopTimer()
    }

    companion object {
        private const val CHANNEL_ID = "audio_player_channel"
        private const val NOTIFICATION_ID = 1
        
        const val EXTRA_URL = "extra_url"
        const val EXTRA_TRACK_NAME = "extra_track_name"
        const val EXTRA_ARTIST_NAME = "extra_artist_name"
    }
}