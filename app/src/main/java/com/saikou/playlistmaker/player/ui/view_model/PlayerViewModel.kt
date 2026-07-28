package com.saikou.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saikou.playlistmaker.media_libr.domain.FavoriteInteractor
import com.saikou.playlistmaker.media_libr.domain.api.PlaylistInteractor
import com.saikou.playlistmaker.media_libr.domain.models.Playlist
import com.saikou.playlistmaker.player.data.PlayerState
import com.saikou.playlistmaker.player.data.PlayerStateEnum
import com.saikou.playlistmaker.player.ui.service.AudioPlayerControl
import com.saikou.playlistmaker.search.data.entity.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playerStateLiveData =
        MutableLiveData<PlayerState>(PlayerState(PlayerStateEnum.STATE_DEFAULT, "00:00"))

    private val isFavoriteLiveData = MutableLiveData<Boolean>(track.isFavorite)

    private val playlistsLiveData = MutableLiveData<List<Playlist>>()

    private val addTrackStatusLiveData = MutableLiveData<Pair<String, Boolean>>()

    private var audioPlayerService: AudioPlayerControl? = null
    private var serviceJob: Job? = null

    init {
        checkFavoriteStatus()
        loadPlaylists()
    }

    fun onServiceConnected(service: AudioPlayerControl) {
        audioPlayerService = service

        serviceJob = viewModelScope.launch {
            audioPlayerService?.getPlayerState()?.collect { state ->
                playerStateLiveData.postValue(state)
            }
        }
    }

    fun onServiceDisconnected() {
        audioPlayerService = null
        serviceJob?.cancel()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect {
                playlistsLiveData.postValue(it)
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addTrackToPlaylist(playlist, track).collect { added ->
                addTrackStatusLiveData.postValue(Pair(playlist.name, added))
                if (added) {
                    loadPlaylists()
                }
            }
        }
    }

    fun observePlaylists(): LiveData<List<Playlist>> = playlistsLiveData

    fun observeAddTrackStatus(): LiveData<Pair<String, Boolean>> = addTrackStatusLiveData

    private fun checkFavoriteStatus() {
        viewModelScope.launch {
            favoriteInteractor.getFavoriteTrackIds().collect { favoriteIds ->
                isFavoriteLiveData.postValue(favoriteIds.contains(track.trackId))
            }
        }
    }

    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    fun observeIsFavorite(): LiveData<Boolean> = isFavoriteLiveData

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value?.state) {
            PlayerStateEnum.STATE_PLAYING -> audioPlayerService?.pause()
            PlayerStateEnum.STATE_PREPARED, PlayerStateEnum.STATE_PAUSED -> audioPlayerService?.play()
            else -> {}
        }
    }

    fun onFavoriteButtonClicked() {
        viewModelScope.launch {
            if (isFavoriteLiveData.value == true) {
                favoriteInteractor.deleteTrack(track)
                isFavoriteLiveData.postValue(false)
            } else {
                favoriteInteractor.addTrack(track)
                isFavoriteLiveData.postValue(true)
            }
        }
    }

    fun onPause() {
        audioPlayerService?.pause()
    }

    fun showNotification() {
        audioPlayerService?.showNotification()
    }

    fun hideNotification() {
        audioPlayerService?.hideNotification()
    }
}