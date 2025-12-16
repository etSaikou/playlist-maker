package com.saikou.playlistmaker

import android.net.Network
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.saikou.playlistmaker.entity.ResponseWrapper
import com.saikou.playlistmaker.entity.Track
import com.saikou.playlistmaker.track_adapter.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class SearchActivity : AppCompatActivity() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
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
    private val trackAdapter = TrackAdapter(trackList)
    private val searchCallback = object : Callback<ResponseWrapper>{
        override fun onResponse(
            call: Call<ResponseWrapper>,
            response: Response<ResponseWrapper>
        ) {
            if (response.code() == 200) {
                trackList.clear()
                if (response.body()?.results?.isNotEmpty() == true) {
                    trackList.addAll(response.body()?.results!!)
                    trackAdapter.notifyDataSetChanged()
                }
                if (trackList.isEmpty()) {
                    showMessage(getString(R.string.search_error_not_found), "", false)
                } else {
                    showMessage("", "", false)
                }
            } else {
                showMessage(
                    getString(R.string.search_error_network),
                    response.code().toString(),
                    true
                )
            }
        }

        override fun onFailure(
            call: Call<ResponseWrapper?>,
            t: Throwable
        ) {
            showMessage(getString(R.string.search_error_network), t.toString(), true)
        }

    }
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
        searchPlaceholder.visibility = View.GONE
        val searchApi = retrofit.create<BackendApi>()


        trackListView.adapter = trackAdapter

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        savedLine = savedInstanceState?.getString(SEARCH_TAG) ?: ""

        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        clearButton.setOnClickListener {
            searchBar.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            inputMethodManager.hideSoftInputFromWindow(searchBar.windowToken, 0)
        }
        searchBar.doAfterTextChanged {
            if (!it.isNullOrEmpty()) {
                savedLine = it.toString()

//                searchApi.search(it.toString()).enqueue(searchCallback)
            }

            clearButton.visibility = if (it.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        refreshButton.setOnClickListener {
            searchApi.search(savedLine).enqueue(searchCallback)
        }
        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchApi.search(searchBar.text.toString()).enqueue(searchCallback)
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

    fun addToList(trackName: String, artistName: String, trackTime: Long, artworkUrl100: String) {
        trackList.add(Track(trackName, artistName, trackTime, artworkUrl100))
    }


}