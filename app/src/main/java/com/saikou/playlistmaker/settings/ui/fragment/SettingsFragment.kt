package com.saikou.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.saikou.playlistmaker.App
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.databinding.FragmentSettingsBinding
import com.saikou.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.saikou.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeTheme().observe(viewLifecycleOwner) {
            binding.vThemeSwitcher.isChecked = it
            (context?.applicationContext as App).switchTheme(it)
        }

        binding.vThemeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.setTheme(checked)
        }
        with(binding) {

            vShareButton.setOnClickListener {
                viewModel.shareApp()
            }

            vSupportButton.setOnClickListener {
                viewModel.openSupport()

            }

            vUserAgreementButton.setOnClickListener {
                findNavController().navigate(
                    R.id.action_settingsFragment_to_termsFragment,
                    TermsFragment.createArgs(viewModel.getLinkTerms())
                )

            }
        }

    }
}