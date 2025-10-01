package com.saikou.playlistmaker

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_settings)
        val privacyButton = findViewById<TextView>(R.id.vUserAgreementButton)
        privacyButton.setOnClickListener {

        }
    }
}