package com.saikou.playlistmaker.track_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.entity.Track
import com.saikou.playlistmaker.global.dpToPx
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder : RecyclerView.ViewHolder {

    constructor(parent: ViewGroup) : super(
        LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
    )

    private val trackName: TextView = itemView.findViewById<TextView>(R.id.vTrackName)
    private val artistName: TextView = itemView.findViewById<TextView>(R.id.vArtistName)
    private val trackTime: TextView = itemView.findViewById<TextView>(R.id.vTime)
    private val artAlbum: ImageView = itemView.findViewById<ImageView>(R.id.vArt)

    fun bind(track: Track) {
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder_45)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(artAlbum)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)


    }

    fun addToHistory(track: Track){

    }

}