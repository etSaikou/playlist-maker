package com.saikou.playlistmaker.player.ui.activity

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.databinding.ActivityPlayerBinding
import com.saikou.playlistmaker.global.Const
import com.saikou.playlistmaker.global.deserialize
import com.saikou.playlistmaker.global.dpToPx
import com.saikou.playlistmaker.global.millisFormat
import com.saikou.playlistmaker.global.replaceDimensionArtwork
import com.saikou.playlistmaker.global.vis
import com.saikou.playlistmaker.player.ui.view_model.PlayerViewModel
import com.saikou.playlistmaker.search.data.entity.Track

class PlayerActivity : AppCompatActivity() {

    private var viewModel: PlayerViewModel? = null
    private lateinit var binding: ActivityPlayerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val trackFromIntent =
            intent.getStringExtra(Const.PLAYER_TRACK_DATA)?.deserialize(Track::class.java)
        trackFromIntent?.let {
            viewModel = ViewModelProvider(
                this,
                PlayerViewModel.getFactory(trackFromIntent)
            )[PlayerViewModel::class.java]

            viewModel?.observeTrack()?.observe(this) {
                Glide.with(this)
                    .load(it.artworkUrl100.replaceDimensionArtwork())
                    .placeholder(R.drawable.ic_placeholder_45)
                    .centerCrop()
                    .transform(RoundedCorners(dpToPx(16f, this)))
                    .into(binding.vAlbumArt)

                binding.apply {
                    vTrackNamePlayer.text = it.trackName
                    vArtistName.text = it.artistName

                    vAlbumNameTitle.vis(it.collectionName.isNotEmpty())
                    vAlbumNameContent.vis(it.collectionName.isNotEmpty())
                    vYearTitle.vis(it.releaseDate.isNotEmpty())
                    vYearContent.vis(it.releaseDate.isNotEmpty())

                    vAlbumNameContent.text = it.collectionName.ifEmpty { "-" }
                    vYearContent.text = it.releaseDate.substringBefore('-').ifEmpty { "-" }
                    vGenreContent.text = it.primaryGenreName
                    vDurationContent.text = it.trackTimeMillis.millisFormat() ?: "0:00"
                    vCountryContent.text = it.country
                }
            }

            viewModel?.observeProgressTime()?.observe(this) {
                binding.vTrackTime.text = it
            }
            viewModel?.observePlayerState()?.observe(this) {
                changeButton(it == PlayerViewModel.STATE_PLAYING)
                binding.vPlayButton.isEnabled = (it != PlayerViewModel.STATE_DEFAULT)
            }

        }

        binding.vPlayButton.setOnClickListener {
            viewModel?.onPlayButtonClicked()
        }

    }

    private fun changeButton(isPlaying: Boolean) {
        Glide.with(this)
            .load(if (isPlaying) R.drawable.ic_play_button_light_pause_83 else R.drawable.ic_play_button_light_83)
            .into(binding.vPlayButton)
    }

    override fun onPause() {
        super.onPause()
        viewModel?.onPause()
    }
}