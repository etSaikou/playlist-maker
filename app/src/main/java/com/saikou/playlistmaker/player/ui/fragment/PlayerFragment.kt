package com.saikou.playlistmaker.player.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.databinding.FragmentPlayerBinding
import com.saikou.playlistmaker.global.deserialize
import com.saikou.playlistmaker.global.dpToPx
import com.saikou.playlistmaker.global.millisFormat
import com.saikou.playlistmaker.global.replaceDimensionArtwork
import com.saikou.playlistmaker.global.vis
import com.saikou.playlistmaker.player.data.PlayerStateEnum
import com.saikou.playlistmaker.player.ui.view_model.PlayerViewModel
import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue
import kotlin.text.ifEmpty

class PlayerFragment : BindingFragment<FragmentPlayerBinding>() {

    private val trackFromIntent by lazy(mode = LazyThreadSafetyMode.NONE) {
        requireArguments().getString(ARGS_TRACK)?.deserialize(
            Track::class.java
        )
    }
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(trackFromIntent?.previewUrl)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackFromIntent?.let {

            Glide.with(this)
                .load(it.artworkUrl100.replaceDimensionArtwork())
                .placeholder(R.drawable.ic_placeholder_45)
                .centerCrop()
                .transform(RoundedCorners(dpToPx(16f, context)))
                .into(binding.vAlbumArt)

            with(binding) {
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

                binding.vPlayButton.setOnClickListener {
                    viewModel.onPlayButtonClicked()
                }
            }

        }

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            changeButton(it.state == PlayerStateEnum.STATE_PLAYING)
            binding.vPlayButton.isEnabled = (it.state != PlayerStateEnum.STATE_DEFAULT)
            binding.vTrackTime.text = it.timer
        }
    }

    private fun changeButton(isPlaying: Boolean) {
        Glide.with(this)
            .load(if (isPlaying) R.drawable.ic_play_button_light_pause_83 else R.drawable.ic_play_button_light_83)
            .into(binding.vPlayButton)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    companion object {
        private const val ARGS_TRACK = "track_info"

        fun createArgs(track: String): Bundle =
            bundleOf(ARGS_TRACK to track)
    }
}