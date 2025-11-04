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
import com.google.android.material.bottomnavigation.BottomNavigationView

class SearchActivity : AppCompatActivity() {
    private val bottomBar by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<BottomNavigationView>(R.id.vNavMenu) }
    private val searchBar by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<EditText>(R.id.vSearchLine) }
    private val clearButton by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<ImageButton>(R.id.vClearButton) }
    private val toolbar by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<Toolbar>(R.id.toolbar) }
    private lateinit var savedLine: String

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


}