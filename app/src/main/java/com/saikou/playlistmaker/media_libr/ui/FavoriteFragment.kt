package com.saikou.playlistmaker.media_libr.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.databinding.FragmentFavoriteBinding
import com.saikou.playlistmaker.global.Const
import com.saikou.playlistmaker.global.serialize
import com.saikou.playlistmaker.global.vis
import com.saikou.playlistmaker.media_libr.ui.models.FavoriteState
import com.saikou.playlistmaker.media_libr.ui.view_model.FavoriteViewModel
import com.saikou.playlistmaker.player.ui.fragment.PlayerFragment
import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.search.ui.track_adapter.TrackAdapter
import com.saikou.playlistmaker.util.BindingFragment
import com.saikou.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : BindingFragment<FragmentFavoriteBinding>() {

    private val viewModel: FavoriteViewModel by viewModel()
    private var trackAdapter: TrackAdapter? = null
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteBinding {
        return FragmentFavoriteBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(Const.CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            findNavController().navigate(
                R.id.action_mediaFragment_to_playerFragment,
                PlayerFragment.createArgs(track.serialize() ?: "")
            )
        }

        trackAdapter = TrackAdapter {
            viewModel.addToHistory(it)
            onTrackClickDebounce(it)
        }

        binding.vTrackList.adapter = trackAdapter

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.Content -> showContent(state.tracks)
            is FavoriteState.Empty -> showEmpty()
        }
    }

    private fun showContent(tracks: List<Track>) {
        binding.vPlaceholder.vis(false)
        binding.vTrackList.vis(true)

        trackAdapter?.load(tracks)
    }

    private fun showEmpty() {
        binding.vPlaceholder.vis(true)
        binding.vTrackList.vis(false)
    }


    override fun onDestroyView() {
        binding.vTrackList.adapter = null
        trackAdapter = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = FavoriteFragment().apply {
            arguments = Bundle()
        }
    }
}
