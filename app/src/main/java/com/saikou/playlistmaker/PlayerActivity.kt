package com.saikou.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.saikou.playlistmaker.entity.Track
import com.saikou.playlistmaker.global.Const
import com.saikou.playlistmaker.global.deserialize
import com.saikou.playlistmaker.global.dpToPx
import com.saikou.playlistmaker.global.millisFormat
import com.saikou.playlistmaker.global.replaceDimensionArtwork
import com.saikou.playlistmaker.global.vis

class PlayerActivity : AppCompatActivity() {

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
        }


    }
}