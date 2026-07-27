package com.saikou.playlistmaker.media_libr.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.saikou.playlistmaker.global.dpToPx
import com.saikou.playlistmaker.global.millisFormat
import com.saikou.playlistmaker.global.replaceDimensionArtwork
import com.saikou.playlistmaker.global.serialize
import com.saikou.playlistmaker.global.showToast
import com.saikou.playlistmaker.global.vis
import com.saikou.playlistmaker.media_libr.domain.models.Playlist
import com.saikou.playlistmaker.media_libr.ui.view_model.PlaylistDetailsViewModel
import com.saikou.playlistmaker.player.ui.fragment.PlayerFragment
import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.search.ui.track_adapter.TrackAdapter
import com.saikou.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistDetailsFragment : BindingFragment<FragmentPlaylistDetailsBinding>() {

    private val playlistId by lazy { requireArguments().getInt(ARGS_PLAYLIST_ID) }
    private val viewModel: PlaylistDetailsViewModel by viewModel { parametersOf(playlistId) }

    private var adapter: TrackAdapter? = null
    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        updatePeekHeight()
    }



    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistDetailsBinding {
        return FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)

        setupAdapters()
        setupBottomSheets()
        setupListeners()

        viewModel.observePlaylist().observe(viewLifecycleOwner) { playlist ->
            renderPlaylistInfo(playlist)
        }

        viewModel.observeTracks().observe(viewLifecycleOwner) { tracks ->
            renderTracks(tracks)
        }
    }

    private fun setupAdapters() {
        adapter = TrackAdapter(
            onItemClicked = { track ->
                findNavController().navigate(
                    R.id.action_playlistDetailsFragment_to_playerFragment,
                    PlayerFragment.createArgs(track.serialize() ?: "")
                )
            },
            onItemLongClicked = { track ->
                showDeleteTrackDialog(track)
            }
        )
        binding.tracksBottomSheet.vTracksRecyclerView.adapter = adapter
    }

    private fun setupBottomSheets() {
        val tracksBottomSheetView = binding.tracksBottomSheet.root
        tracksBottomSheetBehavior = BottomSheetBehavior.from(tracksBottomSheetView)

        updatePeekHeight()

        val menuBottomSheetView = binding.menuBottomSheet.root
        menuBottomSheetBehavior = BottomSheetBehavior.from(menuBottomSheetView).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.vOverlay.vis(newState != BottomSheetBehavior.STATE_HIDDEN)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.vOverlay.alpha = slideOffset
            }
        })
    }

    private fun updatePeekHeight() {
        if (_binding == null) return
        binding.root.post {
            if (_binding == null) return@post
            val shareButtonBottom = binding.vShareButton.bottom
            val amountOfPeekHeight =
                binding.root.height - shareButtonBottom - resources.getDimensionPixelSize(R.dimen.margin_24dp)
            if (tracksBottomSheetBehavior.peekHeight != amountOfPeekHeight) {
                tracksBottomSheetBehavior.peekHeight = amountOfPeekHeight
            }
        }
    }

    private fun setupListeners() {

        binding.vBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.vShareButton.setOnClickListener {
            sharePlaylist()
        }

        binding.vMenuButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        binding.menuBottomSheet.vShareMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }

        binding.menuBottomSheet.vEditMenu.setOnClickListener {
            viewModel.observePlaylist().value?.let { playlist ->
                findNavController().navigate(
                    R.id.action_playlistDetailsFragment_to_editPlaylistFragment,
                    bundleOf(EditPlaylistFragment.ARGS_PLAYLIST to playlist.serialize())
                )
            }
        }

        binding.menuBottomSheet.vDeleteMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }
    }

    private fun renderPlaylistInfo(playlist: Playlist) {
        binding.vPlaylistName.text = playlist.name
        binding.vPlaylistDescription.text = playlist.description
        binding.vPlaylistDescription.vis(playlist.description.isNotEmpty())

        binding.vTracksCount.text = resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksCount,
            playlist.tracksCount
        )
        Glide.with(this)
            .load(playlist.imagePath)
            .placeholder(R.drawable.ic_placeholder_45)
            .centerCrop()
            .into(binding.vPlaylistCover)
        Glide.with(this)
            .load(playlist.imagePath)
            .placeholder(R.drawable.ic_placeholder_45)
            .centerCrop()
            .into(binding.menuBottomSheet.includedPlaylistInfo.vPlaylistCover)


        binding.menuBottomSheet.includedPlaylistInfo.vPlaylistName.text = playlist.name
        binding.menuBottomSheet.includedPlaylistInfo.vTracksCount.text = binding.vTracksCount.text
    }

    private fun renderTracks(tracks: List<Track>) {
        adapter?.load(tracks)
        binding.tracksBottomSheet.vEmptyMessage.vis(tracks.isEmpty())

        val totalMillis = tracks.sumOf { it.trackTimeMillis }
        val totalMinutes = (totalMillis / 60000).toInt()
        binding.vPlaylistDuration.text = resources.getQuantityString(
            R.plurals.minutes_count,
            totalMinutes,
            totalMinutes
        )
    }

    private fun sharePlaylist() {
        val tracks = viewModel.observeTracks().value ?: emptyList()
        if (tracks.isEmpty()) {
            showToast(requireContext(), getString(R.string.playlist_empty_share_msg))
        } else {
            viewModel.sharePlaylist(
                tracks,
                binding.vTracksCount.text.toString(),
                { millis -> millis.millisFormat() ?: "0:00" }
            )
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
            .setTitle(R.string.track_delete_dialog_msg)
            .setMessage(" ")
            .setNegativeButton(R.string.no) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteTrack(track.trackId)
            }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
            .setTitle(R.string.playlist_delete_dialog_title)
            .setMessage(R.string.playlist_delete_dialog_msg)
            .setNegativeButton(R.string.playlist_create_dialog_cancel) { _, _ -> }
            .setPositiveButton(R.string.playlist_delete_confirm) { _, _ ->
                viewModel.deletePlaylist()
                findNavController().popBackStack()
            }
            .show()
    }

    override fun onDestroyView() {
        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
        super.onDestroyView()
        adapter = null
    }

    companion object {
        const val ARGS_PLAYLIST_ID = "playlist_id"
    }
}
