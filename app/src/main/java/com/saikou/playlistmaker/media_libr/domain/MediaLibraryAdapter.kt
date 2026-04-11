package com.saikou.playlistmaker.media_libr.domain

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.saikou.playlistmaker.media_libr.ui.FavoriteFragment
import com.saikou.playlistmaker.media_libr.ui.PlaylistFragment

class MediaLibraryAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,

) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavoriteFragment.newInstance()
            else -> PlaylistFragment.newInstance()
        }
    }
}