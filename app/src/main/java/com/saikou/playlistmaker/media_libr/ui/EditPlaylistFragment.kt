package com.saikou.playlistmaker.media_libr.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.global.deserialize
import com.saikou.playlistmaker.media_libr.domain.models.Playlist

class EditPlaylistFragment : CreatePlaylistFragment() {

    private val playlist by lazy {
        requireArguments().getString(ARGS_PLAYLIST)?.deserialize(Playlist::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vCreateButton.text = getString(R.string.playlist_save)

        playlist?.let {
            binding.vPlaylistName.setText(it.name)
            binding.vPlaylistDescription.setText(it.description)
            if (it.imagePath.isNullOrEmpty()) {
                binding.vCardCover.setBackgroundColor(resources.getColor(R.color.transparent, context?.theme))
            }
            Glide.with(this)
                .load(it.imagePath)
                .placeholder(R.drawable.ic_placeholder_235)
                .centerCrop()
                .into(binding.vPlaylistCover)
        }
    }

    override fun savePlaylist() {
        val name = binding.vPlaylistName.text.toString()
        val description = binding.vPlaylistDescription.text.toString()
        playlist?.let {
            viewModel.updatePlaylist(it, name, description, imageUri)
        }
        findNavController().popBackStack()
    }

    companion object {
        const val ARGS_PLAYLIST = "playlist"
    }
}
