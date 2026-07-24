package com.saikou.playlistmaker.media_libr.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.saikou.playlistmaker.databinding.FragmentFavoriteBinding
import com.saikou.playlistmaker.media_libr.ui.view_model.FavoriteViewModel
import com.saikou.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment: BindingFragment<FragmentFavoriteBinding>() {

    private val viewModel: FavoriteViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteBinding {
        return FragmentFavoriteBinding.inflate(inflater, container, false)
    }

    companion object {

        fun newInstance() = FavoriteFragment().apply {
            arguments = Bundle()
        }
    }
}