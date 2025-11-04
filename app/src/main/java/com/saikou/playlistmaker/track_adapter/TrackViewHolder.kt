package com.saikou.playlistmaker.track_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.entity.Track

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    constructor(parent: ViewGroup) : this(
        LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
    )

    private val trackName: TextView = itemView.findViewById<TextView>(R.id.vTrackName)
    private val artistNameTime: TextView = itemView.findViewById<TextView>(R.id.vArtistTime)
    private val artAlbum: ImageView = itemView.findViewById<ImageView>(R.id.vArt)


    fun bind(track: Track) {
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder_45)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(artAlbum)

        trackName.text = track.trackName
        artistNameTime.text = String.format(
            itemView.context.getString(R.string.track_divider),
            track.artistName,
            track.trackTime
        )
    }

}