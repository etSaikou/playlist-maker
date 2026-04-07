package com.saikou.playlistmaker.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.data.track.entity.Track
import com.saikou.playlistmaker.global.Const
import com.saikou.playlistmaker.global.deserialize
import com.saikou.playlistmaker.global.dpToPx
import com.saikou.playlistmaker.global.millisFormat
import com.saikou.playlistmaker.global.replaceDimensionArtwork
import com.saikou.playlistmaker.global.vis
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private var mediaPlayer = MediaPlayer()
    private val playButton by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<ImageButton>(R.id.vPlayButton) }
    private val trackTime by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.vTrackTime) }
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val playerHandler = Handler(Looper.getMainLooper())
    private val trackTimeRunnable = object: Runnable {
        override fun run() {
            trackTime.text = dateFormat.format(mediaPlayer.currentPosition) ?: "00:00"
            playerHandler.postDelayed(this, Const.PLAYER_CHECK)
        }

    }

    private var playerState = STATE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_player)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        if (playerState == STATE_DEFAULT) {
            playButton.isEnabled = false
        }

        val albumArt = findViewById<ImageView>(R.id.vAlbumArt)
        val trackName = findViewById<TextView>(R.id.vTrackNamePlayer)
        val artistName = findViewById<TextView>(R.id.vArtistName)

        val albumName = findViewById<TextView>(R.id.vAlbumNameContent)
        val albumNameTitle = findViewById<TextView>(R.id.vAlbumNameTitle)

        val releaseDate = findViewById<TextView>(R.id.vYearContent)
        val releaseDateTitle = findViewById<TextView>(R.id.vYearTitle)

        val genre = findViewById<TextView>(R.id.vGenreContent)
        val duration = findViewById<TextView>(R.id.vDurationContent)
        val country = findViewById<TextView>(R.id.vCountryContent)


        val trackFromIntent =
            intent.getStringExtra(Const.PLAYER_TRACK_DATA)?.deserialize(Track::class.java)
        trackFromIntent?.let {

            Glide.with(this)
                .load(it.artworkUrl100.replaceDimensionArtwork())
                .placeholder(R.drawable.ic_placeholder_45)
                .centerCrop()
                .transform(RoundedCorners(dpToPx(16f, this)))
                .into(albumArt)

            trackName.text = it.trackName
            artistName.text = it.artistName

            albumNameTitle.vis(it.collectionName.isNotEmpty())
            albumName.vis(it.collectionName.isNotEmpty())
            releaseDate.vis(it.releaseDate.isNotEmpty())
            releaseDateTitle.vis(it.releaseDate.isNotEmpty())

            albumName.text = it.collectionName.ifEmpty { "-" }
            releaseDate.text = it.releaseDate.substringBefore('-').ifEmpty { "-" }
            genre.text = it.primaryGenreName
            duration.text = it.trackTimeMillis.millisFormat() ?: "0:00"
            country.text = it.country


            val url = it.previewUrl
            preparePlayer(url)


        }

        playButton.setOnClickListener {
            playbackControl()
        }

    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            trackTime.text = getString(R.string.zero_timer)
            playerHandler.removeCallbacks(trackTimeRunnable)
            Glide.with(this)
                .load(R.drawable.ic_play_button_light_83)
                .into(playButton)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        Glide.with(this)
            .load(R.drawable.ic_play_button_light_pause_83)
            .into(playButton)
        playerState = STATE_PLAYING
        playerHandler.post(trackTimeRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        Glide.with(this)
            .load(R.drawable.ic_play_button_light_83)
            .into(playButton)
        playerState = STATE_PAUSED
        playerHandler.removeCallbacks(trackTimeRunnable)


    }

    private fun playbackControl() {

        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()

            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()

            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        playerHandler.removeCallbacks(trackTimeRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        playerHandler.removeCallbacks(trackTimeRunnable)
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}