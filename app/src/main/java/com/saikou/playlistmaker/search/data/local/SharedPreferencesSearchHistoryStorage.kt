package com.saikou.playlistmaker.search.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.saikou.playlistmaker.global.Const
import com.saikou.playlistmaker.global.deserializeToList
import com.saikou.playlistmaker.global.reAdd
import com.saikou.playlistmaker.global.removeFirst
import com.saikou.playlistmaker.global.serialize
import com.saikou.playlistmaker.search.data.entity.TrackHistoryDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SharedPreferencesSearchHistoryStorage(private val sharedPreferences: SharedPreferences): SearchHistoryStorage {

    private val trackList = mutableSetOf<TrackHistoryDto>()

    override suspend fun getTracksHistory(): List<TrackHistoryDto> {
        withContext(Dispatchers.IO) {
            setList()
        }
        return trackList.toList()
    }

    override suspend fun addTrack(track: TrackHistoryDto) {
        withContext(Dispatchers.IO) {
            setList()
            if (trackList.size == 10) {
                trackList.removeFirst()
            }
            if (trackList.contains(track)){
                trackList.reAdd(track)
            }
            trackList.add(track)

            sharedPreferences.edit {
                putString(Const.LAST_SEARCH, trackList.serialize())
            }
        }
    }

    override suspend fun clearHistory() {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit { putString(Const.LAST_SEARCH, "") }
        }
    }

    private suspend fun setList() {
        withContext(Dispatchers.IO) {
            trackList.clear()
            try {
                trackList.addAll(sharedPreferences.getString(Const.LAST_SEARCH, "")?.deserializeToList(
                    TrackHistoryDto::class.java)
                    ?: emptyList())
            } catch (e: Throwable) {

            }
        }
    }

}