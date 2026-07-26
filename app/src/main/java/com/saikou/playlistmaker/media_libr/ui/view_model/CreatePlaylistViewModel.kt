package com.saikou.playlistmaker.media_libr.ui.view_model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saikou.playlistmaker.media_libr.domain.api.PlaylistInteractor
import com.saikou.playlistmaker.media_libr.domain.models.Playlist
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val context: Context
) : ViewModel() {

    fun createPlaylist(name: String, description: String, imageUri: Uri?) {
        viewModelScope.launch {
            val imagePath = imageUri?.let { saveImageToPrivateStorage(it, name) }
            playlistInteractor.createPlaylist(
                Playlist(
                    id = 0,
                    name = name,
                    description = description,
                    imagePath = imagePath,
                    trackIds = emptyList(),
                    tracksCount = 0
                )
            )
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri, playlistName: String): String {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlists")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "cover_${playlistName}_${System.currentTimeMillis()}.jpg")
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.absolutePath
    }
}
