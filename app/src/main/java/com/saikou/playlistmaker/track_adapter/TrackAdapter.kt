package com.saikou.playlistmaker.track_adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saikou.playlistmaker.entity.Track

class TrackAdapter(private val trackList: List<Track>) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int
    ) {
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}