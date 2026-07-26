package com.saikou.playlistmaker.search.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.saikou.playlistmaker.global.Const
import com.saikou.playlistmaker.global.deserializeToList
import com.saikou.playlistmaker.global.serialize
import com.saikou.playlistmaker.search.data.entity.TrackHistoryDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SharedPreferencesSearchHistoryStorage(private val sharedPreferences: SharedPreferences) :
    SearchHistoryStorage {

    override suspend fun getTracksHistory(): List<TrackHistoryDto> = withContext(Dispatchers.IO) {
        getHistoryFromStorage()
    }

    override suspend fun addTrack(track: TrackHistoryDto) = withContext(Dispatchers.IO) {
        val trackList = getHistoryFromStorage().toMutableList()

        trackList.removeIf { it.trackId == track.trackId }

        if (trackList.size >= 10) {
            trackList.removeAt(0)
        }

        trackList.add(track)

        sharedPreferences.edit {
            putString(Const.LAST_SEARCH, trackList.serialize())
        }
    }

    override fun clearHistory() = sharedPreferences.edit { putString(Const.LAST_SEARCH, "") }


    private fun getHistoryFromStorage(): List<TrackHistoryDto> {
        val json = sharedPreferences.getString(Const.LAST_SEARCH, "")
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                json.deserializeToList(TrackHistoryDto::class.java)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
