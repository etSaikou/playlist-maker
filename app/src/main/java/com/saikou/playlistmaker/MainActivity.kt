package com.saikou.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.saikou.playlistmaker.MainActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val searchButton = findViewById<MaterialButton>(R.id.vSearchButton)
        val mediaButton = findViewById<MaterialButton>(R.id.vMediaButton)
        val settingsButton = findViewById<MaterialButton>(R.id.vSettingsButton)

        val onClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                toast("TOASTY~~! Anon")?.show()
            }

        }

        searchButton.setOnClickListener {
            toast("Toasty~~!! Lambda")?.show()
            this.startActivity(Intent(this, SearchActivity::class.java))
        }

        mediaButton.setOnClickListener(onClickListener)

        settingsButton.setOnClickListener {
            toast("SETTINGS TOAST")?.show()
        }

    }
    fun toast(string: String): Toast? = Toast.makeText(this@MainActivity,string, Toast.LENGTH_SHORT)

}

