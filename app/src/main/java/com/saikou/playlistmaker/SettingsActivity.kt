package com.saikou.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import com.saikou.playlistmaker.global.Const
import androidx.core.content.edit

class SettingsActivity : AppCompatActivity() {
    private val agreementButton by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.vUserAgreementButton) }
    private val shareButton by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.vShareButton) }
    private val supportButton by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.vSupportButton) }
    private val toolbar by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<Toolbar>(R.id.toolbar) }
    private val sharedPreferences by lazy(mode = LazyThreadSafetyMode.NONE) { getSharedPreferences(Const.SHARED_PREFS, MODE_PRIVATE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.vThemeSwitcher)
        themeSwitcher.isChecked = sharedPreferences.getBoolean(Const.DARK_THEME_KEY, false)

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPreferences.edit { putBoolean(Const.DARK_THEME_KEY, checked) }
        }

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.android_dev_link))
            shareIntent.setType("text/plain")
            startActivity(shareIntent)


        }

        supportButton.setOnClickListener {
            val supIntent = Intent(Intent.ACTION_SENDTO)
            supIntent.data = "mailto:".toUri()
            supIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
            supIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.support_subject_placeholder)
            )
            supIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_msg_placeholder))
            startActivity(supIntent)

        }

        agreementButton.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, getString(R.string.agreement_link).toUri()))
        }


    }


}