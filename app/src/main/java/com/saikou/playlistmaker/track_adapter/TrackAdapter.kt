package com.saikou.playlistmaker.track_adapter

import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.saikou.playlistmaker.entity.Track
import com.saikou.playlistmaker.global.SearchHistory
import okhttp3.Call

class TrackAdapter(private val trackList: List<Track>) :
    RecyclerView.Adapter<TrackViewHolder>() {

    private lateinit var callback: (Track)-> Unit

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

        holder.itemView.setOnClickListener {
            callback(trackList[position])
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    fun onItemClickCallback(onClickCallback: (Track)-> Unit) {
       callback =  onClickCallback
    }
}