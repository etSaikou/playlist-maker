package com.saikou.playlistmaker.search.ui.fragment

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.databinding.FragmentSearchBinding
import com.saikou.playlistmaker.global.Const
import com.saikou.playlistmaker.global.serialize
import com.saikou.playlistmaker.global.vis
import com.saikou.playlistmaker.player.ui.fragment.PlayerFragment
import com.saikou.playlistmaker.search.data.entity.Track
import com.saikou.playlistmaker.search.data.entity.TrackState
import com.saikou.playlistmaker.search.ui.track_adapter.TrackAdapter
import com.saikou.playlistmaker.search.ui.view_model.SearchViewModel
import com.saikou.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BindingFragment<FragmentSearchBinding>() {

    private val viewModel by viewModel<SearchViewModel>()
    private var textWatcher: TextWatcher? = null

    private lateinit var savedLine: String

    private val trackAdapter = TrackAdapter {
        viewModel.addToHistory(it)
        openPlayer(it)
    }.apply {
        load(emptyList())
    }

    private val clickHandler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vTrackList.adapter = trackAdapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
        }

        binding.vSearchPlaceholder.visibility = View.GONE

        binding.vClearHistory.setOnClickListener {
            viewModel.clearHistory()
            trackAdapter.clear()
            it.visibility = View.GONE
            binding.vHistoryTitle.visibility = View.GONE
        }

        savedLine = savedInstanceState?.getString(SEARCH_TAG) ?: ""

        val inputMethodManager =
            context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        binding.vSearchLine.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && binding.vSearchLine.text.isNullOrEmpty()) {
                viewModel.clearSearch()
            }
        }

        binding.vClearButton.setOnClickListener {
            binding.vSearchLine.setText("")
            trackAdapter.clear()
            inputMethodManager.hideSoftInputFromWindow(binding.vSearchLine.windowToken, 0)
            binding.vSearchPlaceholder.visibility = View.GONE
            viewModel.clearSearch()
        }

        binding.vRefreshButton.setOnClickListener {
            viewModel.searchDebounce(savedLine, true)
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!s.isNullOrEmpty()) {
                    savedLine = s.toString()
                } else {
                    viewModel.clearSearch()
                }
                viewModel.searchDebounce(changedText = s?.toString() ?: "", false)
                binding.vClearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        }
        textWatcher?.let { binding.vSearchLine.addTextChangedListener(it) }
        binding.vSearchLine.setText(savedLine)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TAG, binding.vSearchLine.text.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedLine = savedInstanceState?.getString(SEARCH_TAG) ?: ""
    }

    private fun openPlayer(track: Track) {
        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(track.serialize() ?: "")
            )
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            clickHandler.postDelayed({ isClickAllowed = true }, Const.CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun showLoading() {
        binding.vContentWrapper.vis(false)
        binding.vSearchProgress.vis(true)
    }

    fun showContent(trackList: List<Track>) {
        with(binding) {
            vContentWrapper.vis(true)
            vTrackList.vis(true)
            vSearchPlaceholder.vis(false)
            vSearchProgress.vis(false)
            vClearHistory.vis(false)
            vHistoryTitle.vis(false)
        }

        trackAdapter.load(trackList)
    }

    fun showError(errorMessage: String) {
        with(binding) {
            vContentWrapper.vis(true)
            vTrackList.vis(false)
            vSearchPlaceholder.vis(true)
            vPlaceholderText.visibility = View.VISIBLE
            vSearchProgress.vis(false)
            vClearHistory.vis(false)
            vHistoryTitle.vis(false)
            vPlaceholderText.text = errorMessage
            vPlaceholderImage.setImageResource(R.drawable.ic_error_net_light_120)
            vRefreshButton.vis(true)
        }
    }

    fun showEmpty(emptyMessage: String) {

        showError(emptyMessage)

        binding.vPlaceholderImage.setImageResource(R.drawable.ic_error_not_found_light_120)
        binding.vRefreshButton.vis(false)
    }

    fun showHistory(tracks: List<Track>) {

        showContent(tracks.reversed())
        binding.vClearHistory.vis(!tracks.isEmpty())
        binding.vHistoryTitle.vis(!tracks.isEmpty())
    }

    fun showToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun render(state: TrackState) {
        when (state) {
            is TrackState.Loading -> showLoading()
            is TrackState.Content -> showContent(state.tracks)
            is TrackState.Error -> showError(state.message)
            is TrackState.Empty -> showEmpty(state.message)
            is TrackState.History -> showHistory(state.trackHistory)
            is TrackState.LoadingHistory -> showLoading()
        }
    }

    companion object {
        private const val SEARCH_TAG = "search_text"
    }
}