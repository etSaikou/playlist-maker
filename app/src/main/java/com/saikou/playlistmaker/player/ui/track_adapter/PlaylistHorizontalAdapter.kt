package com.saikou.playlistmaker.player.ui.track_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.databinding.PlaylistViewHorizontalBinding
import com.saikou.playlistmaker.global.dpToPx
import com.saikou.playlistmaker.media_libr.domain.models.Playlist

class PlaylistHorizontalAdapter(private val clickListener: (Playlist) -> Unit) :
    RecyclerView.Adapter<PlaylistHorizontalAdapter.PlaylistHorizontalViewHolder>() {

    private var playlists = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHorizontalViewHolder {
        val binding = PlaylistViewHorizontalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistHorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistHorizontalViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener(playlists[position]) }
    }

    override fun getItemCount(): Int = playlists.size

    fun load(newPlaylists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylists)
        notifyDataSetChanged()
    }

    class PlaylistHorizontalViewHolder(private val binding: PlaylistViewHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.vPlaylistName.text = playlist.name
            binding.vTracksCount.text = itemView.resources.getQuantityString(
                R.plurals.tracks_count,
                playlist.tracksCount,
                playlist.tracksCount
            )

            Glide.with(itemView)
                .load(playlist.imagePath)
                .placeholder(R.drawable.ic_placeholder_45)
                .transform(CenterCrop(), RoundedCorners(dpToPx(2f, itemView.context)))
                .into(binding.vPlaylistCover)
        }
    }
}
