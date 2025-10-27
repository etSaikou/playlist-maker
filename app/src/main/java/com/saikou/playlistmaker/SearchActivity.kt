package com.saikou.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class SearchActivity : AppCompatActivity() {
    private val bottomBar by lazy { findViewById<BottomNavigationView>(R.id.vNavMenu) }
    private val searchBar by lazy { findViewById<EditText>(R.id.vSearchLine) }
    private val clearButton by lazy { findViewById<ImageButton>(R.id.vClearButton) }
    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
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

        val searchWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) savedLine = s.toString()

                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

        }

        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager


        clearButton.setOnClickListener {
            searchBar.setText("")
            inputMethodManager.hideSoftInputFromWindow(searchBar.windowToken, 0)
        }

        searchBar.addTextChangedListener(searchWatcher)
        searchBar.setText(savedLine)

        bottomBar.visibility = View.GONE
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedLine = savedInstanceState.getString(SEARCH_TAG) ?: ""
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (!searchBar.text.isNullOrEmpty()) {
            outState.putString(SEARCH_TAG, searchBar.text.toString())
        }
    }


}