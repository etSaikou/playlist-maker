package com.saikou.playlistmaker.media_libr.ui.track_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.media_libr.domain.models.Playlist

class PlaylistAdapter(private val clickListener: PlaylistClickListener) : RecyclerView.Adapter<PlaylistViewHolder>() {

    private var playlists = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_view, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.onPlaylistClick(playlists[position]) }
    }

    override fun getItemCount(): Int = playlists.size

    fun load(newPlaylists: List<Playlist>) {
        val diffResult = DiffUtil.calculateDiff(PlaylistDiffCallback(playlists, newPlaylists))
        playlists.clear()
        playlists.addAll(newPlaylists)
        diffResult.dispatchUpdatesTo(this)
    }

    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }

    private class PlaylistDiffCallback(
        private val oldList: List<Playlist>,
        private val newList: List<Playlist>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
