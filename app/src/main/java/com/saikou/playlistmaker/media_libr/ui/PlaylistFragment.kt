package com.saikou.playlistmaker.media_libr.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.databinding.FragmentPlaylistBinding
import com.saikou.playlistmaker.global.vis
import com.saikou.playlistmaker.media_libr.domain.models.Playlist
import com.saikou.playlistmaker.media_libr.ui.models.PlaylistState
import com.saikou.playlistmaker.media_libr.ui.track_adapter.PlaylistAdapter
import com.saikou.playlistmaker.media_libr.ui.view_model.PlaylistViewModel
import com.saikou.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {

    private val viewModel: PlaylistViewModel by viewModel()
    private var adapter: PlaylistAdapter? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlaylistAdapter { playlist ->
            // TODO: click
        }
        binding.vPlaylistList.adapter = adapter

        binding.vAddButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_createPlaylistFragment)
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> showContent(state.playlists)
            is PlaylistState.Empty -> showEmpty()
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.vPlaceholder.vis(false)
        binding.vPlaylistList.vis(true)
        adapter?.load(playlists)
    }

    private fun showEmpty() {
        binding.vPlaceholder.vis(true)
        binding.vPlaylistList.vis(false)
    }

    override fun onDestroyView() {
        binding.vPlaylistList.adapter = null
        adapter = null
        super.onDestroyView()
    }

    companion object {

        fun newInstance() = PlaylistFragment().apply {
            arguments = Bundle()
        }
    }

}
