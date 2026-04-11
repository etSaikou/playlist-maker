package com.saikou.playlistmaker.media_libr.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.saikou.playlistmaker.databinding.FragmentPlaylistBinding
import com.saikou.playlistmaker.media_libr.view_model.PlaylistViewModel
import com.saikou.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {

    private val viewModel: PlaylistViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    companion object {

        fun newInstance() = PlaylistFragment().apply {
            arguments = Bundle()
        }
    }

}
