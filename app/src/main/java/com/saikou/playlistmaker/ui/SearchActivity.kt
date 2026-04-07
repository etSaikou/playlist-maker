package com.saikou.playlistmaker.ui

import android.accounts.NetworkErrorException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.saikou.playlistmaker.Creator
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.data.track.entity.TrackSearchResponse
import com.saikou.playlistmaker.data.track.entity.Track
import com.saikou.playlistmaker.domain.api.HistoryRepository
import com.saikou.playlistmaker.domain.api.TrackInteractor
import com.saikou.playlistmaker.global.Const
import com.saikou.playlistmaker.global.serialize
import com.saikou.playlistmaker.global.vis
import com.saikou.playlistmaker.track_adapter.TrackAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.collections.orEmpty





class SearchActivity : AppCompatActivity() {

    private val trackInteractor = Creator.provideTrackInteractor()

    private val bottomBar by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById<BottomNavigationView>(
            R.id.vNavMenu
        )
    }

    private val searchPlaceholder by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById<LinearLayout>(
            R.id.vSearchPlaceholder
        )
    }
    private val placeholderImage by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById<ImageView>(
            R.id.vPlaceholderImage
        )
    }
    private val placeholderText by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.vPlaceholderText) }
    private val refreshButton by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById<MaterialButton>(
            R.id.vRefreshButton
        )
    }
    private val progressBar by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<ProgressBar>(R.id.vSearchProgress) }
    private val contentWrapper by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById<ConstraintLayout>(
            R.id.vContentWrapper
        )
    }
    private val searchBar by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<EditText>(R.id.vSearchLine) }
    private val clearButton by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<ImageButton>(R.id.vClearButton) }
    private val toolbar by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<Toolbar>(R.id.toolbar) }
    private val clearHistory by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById<MaterialButton>(
            R.id.vClearHistory
        )
    }
    private val historyTitle by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById<TextView>(
            R.id.vHistoryTitle
        )
    }
    private val trackListView by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById<RecyclerView>(
            R.id.vTrackList
        )
    }

    private lateinit var history: HistoryRepository
    private lateinit var savedLine: String
    private val trackList = mutableListOf<Track>()
    private val historyList = mutableListOf<Track>()

    private val trackAdapter = TrackAdapter(trackList)
    private val historyAdapter = TrackAdapter(historyList)

    private val searchHandler = Handler(Looper.getMainLooper())
    private val clickHandler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    lateinit var searchRunnable: Runnable

    private val trackConsumer = object : TrackInteractor.TracksConsumer {
        override fun consume(foundTracks: List<Track>?) {

            searchHandler.post {
                searchVisibility(false)
                trackList.clear()
                if (foundTracks == null) {
                    showMessage(
                        getString(R.string.search_error_network),
                        "",
                        true
                    )
                } else if (foundTracks.isEmpty()) {
                    showMessage(getString(R.string.search_error_not_found), "", false)
                } else {
                    showMessage("", "", false)
                    trackList.addAll(foundTracks)
                    trackAdapter.notifyDataSetChanged()
                }

            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        history = Creator.getHistoryRepository(this)
        searchPlaceholder.visibility = View.GONE


        fun searchRequest(request: String) {

            if (request.isNotEmpty()) {
                searchVisibility(true)
                try {
                    trackInteractor.searchTracks(request, trackConsumer)
                } catch (e: IOException) {
                    showMessage(
                        getString(R.string.search_error_network),
                        e.localizedMessage.toString(),
                        true
                    )

                }

            }
        }

        trackAdapter.onItemClickCallback {
            history.addTrack(it)

            openPlayer(this, it)
        }


        Log.e("SOMEZ", history.getTracksHistory().size.toString())


        checkAdapter()

        clearHistory.setOnClickListener {
            history.clearHistory()
            historyList.clear()
            historyAdapter.notifyDataSetChanged()
            it.visibility = View.GONE
            historyTitle.visibility = View.GONE
            checkAdapter()
        }


        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        savedLine = savedInstanceState?.getString(SEARCH_TAG) ?: ""


        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        searchBar.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && searchBar.text.isNullOrEmpty() && historyAdapter.itemCount > 0) {
                setTrackAdapter(true)
            }
        }

        clearButton.setOnClickListener {
            searchBar.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            inputMethodManager.hideSoftInputFromWindow(searchBar.windowToken, 0)
            showMessage("", "", false)
            checkAdapter()
        }


        searchBar.doAfterTextChanged {

            if (!it.isNullOrEmpty()) {
                savedLine = it.toString()
                setTrackAdapter(false)

                searchRunnable = Runnable { searchRequest(it.toString()) }
                searchDebounce(searchRunnable)

            } else {
                setTrackAdapter(true)
            }

            clearButton.visibility = if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
            setTrackAdapter(it.toString().isEmpty())
        }

        refreshButton.setOnClickListener {
            searchRequest(savedLine)
        }
        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRequest(searchBar.text.toString())
                true
            }
            false
        }
        searchBar.setText(savedLine)
        bottomBar.visibility = View.GONE
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedLine = savedInstanceState.getString(SEARCH_TAG) ?: ""
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(SEARCH_TAG, searchBar.text.toString())

    }

    private fun checkAdapter() {
        setTrackAdapter(history.getTracksHistory().isNotEmpty() && trackList.isEmpty())
    }

    private fun setTrackAdapter(isHistory: Boolean) {
        if (isHistory) {
            trackListView.adapter = historyAdapter
            historyList.clear()
            historyList.addAll(history.getTracksHistory().reversed())
            historyAdapter.notifyDataSetChanged()
            historyAdapter.onItemClickCallback {
                openPlayer(this, it)
            }
            if (!historyList.isEmpty()) {
                clearHistory.visibility = View.VISIBLE
                historyTitle.visibility = View.VISIBLE
            }

        } else {
            trackListView.adapter = trackAdapter
            clearHistory.visibility = View.GONE
            historyTitle.visibility = View.GONE
            showMessage("", "", false)
        }
    }

    private fun openPlayer(context: Context, track: Track) {
        if (clickDebounce()) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(Const.PLAYER_TRACK_DATA, track.serialize())
            startActivity(intent)
        }
    }

    private fun showMessage(text: String, additionalMessage: String, isNetwork: Boolean) {
        if (text.isNotEmpty()) {
            searchPlaceholder.visibility = View.VISIBLE
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            placeholderText.text = text
            if (isNetwork) {
                placeholderImage.setImageResource(R.drawable.ic_error_net_light_120)
                refreshButton.visibility = View.VISIBLE
            } else {
                placeholderImage.setImageResource(R.drawable.ic_error_not_found_light_120)
                refreshButton.visibility = View.GONE
            }
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            searchPlaceholder.visibility = View.GONE
        }
    }

    private fun searchDebounce(searchRunnable: Runnable) {
        searchHandler.removeCallbacks(searchRunnable)
        searchHandler.postDelayed(searchRunnable, Const.SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            clickHandler.postDelayed({ isClickAllowed = true }, Const.CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchVisibility(isSearching: Boolean) {
        progressBar.vis(isSearching)
        contentWrapper.vis(!isSearching)
    }

    override fun onDestroy() {
        super.onDestroy()
        searchHandler.removeCallbacks(searchRunnable)
    }

    companion object {
        private const val SEARCH_TAG = "search_text"
    }
}