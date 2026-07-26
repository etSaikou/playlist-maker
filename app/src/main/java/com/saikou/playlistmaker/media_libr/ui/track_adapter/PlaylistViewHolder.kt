package com.saikou.playlistmaker.media_libr.ui.track_adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.databinding.PlaylistViewBinding
import com.saikou.playlistmaker.global.dpToPx
import com.saikou.playlistmaker.media_libr.domain.models.Playlist

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = PlaylistViewBinding.bind(itemView)

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
            .transform(CenterCrop(), RoundedCorners(dpToPx(8f, itemView.context)))
            .into(binding.vPlaylistCover)
    }
}
