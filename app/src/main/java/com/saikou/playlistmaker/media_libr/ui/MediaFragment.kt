package com.saikou.playlistmaker.media_libr.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.databinding.FragmentMedialibBinding
import com.saikou.playlistmaker.media_libr.domain.MediaLibraryAdapter
import com.saikou.playlistmaker.util.BindingFragment

class MediaFragment: BindingFragment<FragmentMedialibBinding>() {
    private lateinit var tabMediator: TabLayoutMediator

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMedialibBinding {
        return FragmentMedialibBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter = MediaLibraryAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = lifecycle,
        )
        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favorite_tab_title)
                1 -> tab.text = getString(R.string.playlist_tab_title)
            }
        }
        tabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()

    }
}