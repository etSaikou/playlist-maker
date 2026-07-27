package com.saikou.playlistmaker.media_libr.ui

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.saikou.playlistmaker.global.showToast
import com.saikou.playlistmaker.media_libr.ui.view_model.CreatePlaylistViewModel
import com.saikou.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

open class CreatePlaylistFragment : BindingFragment<FragmentCreatePlaylistBinding>() {

    protected open val viewModel by viewModel<CreatePlaylistViewModel>()
    protected var imageUri: Uri? = null

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreatePlaylistBinding {
        return FragmentCreatePlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.vPlaylistCover.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                binding.vPlaylistCover.setImageURI(uri)
                imageUri = uri
            }
        }

        binding.vCardCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        requireActivity().findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            .setNavigationOnClickListener {
                handleBackNavigation()
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        })

        binding.vPlaylistName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.vCreateButton.isEnabled = !s.isNullOrBlank()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.vCreateButton.setOnClickListener {
            savePlaylist()
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(0, 0, 0, imeInsets.bottom)
            insets
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            .setNavigationOnClickListener(null)
    }

    private fun handleBackNavigation() {
        val binding = _binding ?: return
        if (binding.vPlaylistName.text?.isNotEmpty() == true ||
            binding.vPlaylistDescription.text?.isNotEmpty() == true ||
            imageUri != null
        ) {
            showConfirmDialog()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun showConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
            .setTitle(R.string.playlist_create_dialog_title)
            .setMessage(R.string.playlist_create_dialog_msg)
            .setNeutralButton(R.string.playlist_create_dialog_cancel) { _, _ -> }
            .setPositiveButton(R.string.playlist_create_dialog_confirm) { _, _ ->
                findNavController().popBackStack()
            }
            .show()
    }

    protected open fun savePlaylist() {
        val name = binding.vPlaylistName.text.toString()
        val description = binding.vPlaylistDescription.text.toString()
        viewModel.createPlaylist(name, description, imageUri)
        showToast(requireContext(), getString(R.string.playlist_created_msg, name))
        findNavController().popBackStack()
    }
}
