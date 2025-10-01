package com.saikou.playlistmaker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val searchButton = findViewById<MaterialButton>(R.id.view_button_search)

        searchButton.setOnClickListener {
            this.startActivity(Intent(this, SearchActivity::class.java))
        }

    }

}