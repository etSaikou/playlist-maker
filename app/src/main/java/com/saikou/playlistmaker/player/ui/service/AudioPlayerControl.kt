package com.saikou.playlistmaker.player.ui.service

import com.saikou.playlistmaker.player.data.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {
    fun play()
    fun pause()
    fun getPlayerState(): StateFlow<PlayerState>
    fun showNotification()
    fun hideNotification()
}