package com.saikou.playlistmaker.player.ui.fragment

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.databinding.FragmentPlayerBinding
import com.saikou.playlistmaker.global.deserialize
import com.saikou.playlistmaker.global.dpToPx
import com.saikou.playlistmaker.global.millisFormat
import com.saikou.playlistmaker.global.replaceDimensionArtwork
import com.saikou.playlistmaker.global.showToast
import com.saikou.playlistmaker.global.vis
import com.saikou.playlistmaker.player.data.PlayerStateEnum
import com.saikou.playlistmaker.player.ui.service.AudioPlayerService
import com.saikou.playlistmaker.player.ui.track_adapter.PlaylistHorizontalAdapter
import com.saikou.playlistmaker.player.ui.view_model.PlayerViewModel
import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.util.BindingFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : BindingFragment<FragmentPlayerBinding>() {

    private val trackFromIntent by lazy(mode = LazyThreadSafetyMode.NONE) {
        requireArguments().getString(ARGS_TRACK)?.deserialize(
            Track::class.java
        )
    }
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(trackFromIntent)
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var adapter: PlaylistHorizontalAdapter? = null
    private var activityToolbar: Toolbar? = null
    private val dimDrawable = ColorDrawable(Color.BLACK).apply {
        alpha = 0
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Handle the result if needed
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlayerService.AudioPlayerBinder
            viewModel.onServiceConnected(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.onServiceDisconnected()
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheet()

        activityToolbar = requireActivity().findViewById(R.id.toolbar)
        activityToolbar?.overlay?.add(dimDrawable)

        adapter = PlaylistHorizontalAdapter { playlist ->
            viewModel.addTrackToPlaylist(playlist)
        }
        binding.includedPlaylistsBottomSheet.vPlaylistsRecyclerView.adapter = adapter

        binding.vCollectionButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        binding.includedPlaylistsBottomSheet.vNewPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_createPlaylistFragment)
        }

        viewModel.observePlaylists().observe(viewLifecycleOwner) { playlists ->
            adapter?.load(playlists)
        }

        activityToolbar?.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.observeAddTrackStatus().observe(viewLifecycleOwner) { (playlistName, added) ->
            if (added) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                showToast(requireContext(),getString(R.string.added_to_playlist, playlistName))
            } else {
                showToast(requireContext(),getString(R.string.already_in_playlist, playlistName))
            }
        }

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

                binding.vFavoriteButton.setOnClickListener {
                    viewModel.onFavoriteButtonClicked()
                }
            }
        }

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            changeButton(it.state == PlayerStateEnum.STATE_PLAYING)
            binding.vPlayButton.isEnabled = (it.state != PlayerStateEnum.STATE_DEFAULT)
            binding.vTrackTime.text = it.timer
        }

        viewModel.observeIsFavorite().observe(viewLifecycleOwner) { isFavorite ->
            changeFavoriteButton(isFavorite)
        }

        bindPlayerService()

        checkAndRequestNotificationPermission()
    }

    private fun bindPlayerService() {
        val intent = Intent(requireContext(), AudioPlayerService::class.java).apply {
            putExtra(AudioPlayerService.EXTRA_URL, trackFromIntent?.previewUrl)
            putExtra(AudioPlayerService.EXTRA_TRACK_NAME, trackFromIntent?.trackName)
            putExtra(AudioPlayerService.EXTRA_ARTIST_NAME, trackFromIntent?.artistName)
        }
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            if (!NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
                showNotificationPermissionDialog()
            }
        }
    }

    private fun showNotificationPermissionDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
            .setTitle(R.string.notification_permission_title)
            .setMessage(R.string.notification_permission_msg)
            .setNegativeButton(R.string.playlist_create_dialog_cancel) { _, _ -> }
            .setPositiveButton(R.string.settings) { _, _ ->
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                }
                startActivity(intent)
            }
            .show()
    }

    override fun onStart() {
        super.onStart()
        viewModel.hideNotification()
    }

    override fun onStop() {
        super.onStop()
        viewModel.showNotification()
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.includedPlaylistsBottomSheet.root).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
                binding.vOverlay.vis(isVisible)
                if (!isVisible) dimDrawable.alpha = 0
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.vOverlay.alpha = slideOffset
                activityToolbar?.let {
                    dimDrawable.setBounds(0, 0, it.width, it.height)
                    dimDrawable.alpha = (slideOffset * 153).toInt()
                }
            }
        })
    }

    private fun changeButton(isPlaying: Boolean) {
        binding.vPlayButton.setState(isPlaying)
    }

    private fun changeFavoriteButton(isFavorite: Boolean) {
        val imageResource = if (isFavorite) {
            R.drawable.ic_favorite_button_light_toggled_51
        } else {
            R.drawable.ic_favorite_button_light_not_toggled_51
        }
        binding.vFavoriteButton.setImageResource(imageResource)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onPause()
        viewModel.hideNotification()
        requireContext().unbindService(serviceConnection)
        viewModel.onServiceDisconnected()
        activityToolbar?.overlay?.remove(dimDrawable)
    }

    companion object {
        private const val ARGS_TRACK = "track_info"

        fun createArgs(track: String): Bundle =
            bundleOf(ARGS_TRACK to track)
    }
}