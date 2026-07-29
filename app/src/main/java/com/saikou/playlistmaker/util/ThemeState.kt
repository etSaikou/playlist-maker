package com.saikou.playlistmaker.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeState {
    private val _isDark = MutableStateFlow(false)
    val isDark = _isDark.asStateFlow()

    fun updateTheme(dark: Boolean) {
        _isDark.value = dark
    }
}