package com.saikou.playlistmaker.settings.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.saikou.playlistmaker.App
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.creator.Creator
import com.saikou.playlistmaker.settings.domain.SettingsInteractor
import com.saikou.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.saikou.playlistmaker.sharing.domain.SharingInteractor

class SettingsActivity : AppCompatActivity() {

    private val agreementButton by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.vUserAgreementButton) }
    private val shareButton by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.vShareButton) }
    private val supportButton by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.vSupportButton) }
    private val toolbar by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById<Toolbar>(R.id.toolbar) }

    private lateinit var viewModel: SettingsViewModel
    private lateinit var settingsInteractor: SettingsInteractor
    private lateinit var sharingInteractor: SharingInteractor

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
        settingsInteractor = Creator.provideSettings(this)
        sharingInteractor = Creator.provideSharingInteractor(this)
        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getFactory(sharingInteractor, settingsInteractor)
        )[SettingsViewModel::class.java]

        viewModel.observeTheme().observe(this) {
            themeSwitcher.isChecked = it
            (applicationContext as App).switchTheme(it)
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.setTheme(checked)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        shareButton.setOnClickListener {
            viewModel.shareApp()
        }

        supportButton.setOnClickListener {
            viewModel.openSupport()

        }

        agreementButton.setOnClickListener {
            viewModel.openTerms()
        }

    }

}