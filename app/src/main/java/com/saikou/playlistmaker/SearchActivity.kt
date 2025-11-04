package com.saikou.playlistmaker

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.saikou.playlistmaker.entity.Track
import com.saikou.playlistmaker.track_adapter.TrackAdapter

class SearchActivity : AppCompatActivity() {
    private val bottomBar by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById<BottomNavigationView>(
            R.id.vNavMenu
        )
    }
    private val searchBar by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<EditText>(R.id.vSearchLine) }
    private val clearButton by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<ImageButton>(R.id.vClearButton) }
    private val toolbar by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<Toolbar>(R.id.toolbar) }
    private val trackListView by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById<RecyclerView>(
            R.id.vTrackList
        )
    }

    private lateinit var savedLine: String
    private val trackList = mutableListOf<Track>()

    companion object {
        private const val SEARCH_TAG = "search_text"
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
        addToList(
            "Smells Like Teen Spirit",
            "Nirvana",
            "5:01",
            "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
        )
        addToList(
            "Billie Jean",
            "Michael Jackson",
            "4:35",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
        )
        addToList(
            "Stayin' Alive",
            "Bee Gees",
            "4:10",
            "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
        )
        addToList(
            "Whole Lotta Love",
            "Led Zeppelin",
            "5:33",
            "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
        )
        addToList(
            "Sweet Child O'Mine",
            "Guns N' Roses",
            "5:03",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
        )
        val trackAdapter = TrackAdapter(trackList)

        trackListView.adapter = trackAdapter

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        savedLine = savedInstanceState?.getString(SEARCH_TAG) ?: ""

        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        clearButton.setOnClickListener {
            searchBar.setText("")
            inputMethodManager.hideSoftInputFromWindow(searchBar.windowToken, 0)
        }
        searchBar.doAfterTextChanged {
            if (!it.isNullOrEmpty()) savedLine = it.toString()

            clearButton.visibility = if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
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

    fun addToList(trackName: String, artistName: String, trackTime: String, artworkUrl100: String) {
        trackList.add(Track(trackName, artistName, trackTime, artworkUrl100))
    }


}