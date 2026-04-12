package com.saikou.playlistmaker.main.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.databinding.ActivityMainBinding
import com.saikou.playlistmaker.global.vis

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavMenu.setupWithNavController(navController)
        binding.toolbar.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.playerFragment, R.id.termsFragment -> {
                    binding.toolbar.navigationIcon =
                        AppCompatResources.getDrawable(this, R.drawable.ic_arrow_back_16)
                    binding.bottomNavMenu.vis(false)
                }

                else -> {
                    binding.toolbar.navigationIcon = null
                    binding.bottomNavMenu.vis(true)
                }
            }
        }

    }

}