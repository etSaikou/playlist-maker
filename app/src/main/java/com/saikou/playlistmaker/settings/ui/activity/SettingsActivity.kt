package com.saikou.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.saikou.playlistmaker.App
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.databinding.ActivitySettingsBinding
import com.saikou.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.observeTheme().observe(this) {
            binding.vThemeSwitcher.isChecked = it
            (applicationContext as App).switchTheme(it)
        }

        binding.vThemeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.setTheme(checked)
        }
        with(binding) {
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            vShareButton.setOnClickListener {
                viewModel.shareApp()
            }

            vSupportButton.setOnClickListener {
                viewModel.openSupport()

            }

            vUserAgreementButton.setOnClickListener {
                viewModel.openTerms()
            }
        }


    }

}