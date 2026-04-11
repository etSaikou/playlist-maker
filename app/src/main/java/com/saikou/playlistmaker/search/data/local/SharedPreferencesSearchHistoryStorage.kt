package com.saikou.playlistmaker.search.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.saikou.playlistmaker.global.Const
import com.saikou.playlistmaker.global.deserializeToList
import com.saikou.playlistmaker.global.reAdd
import com.saikou.playlistmaker.global.removeFirst
import com.saikou.playlistmaker.global.serialize
import com.saikou.playlistmaker.search.data.entity.TrackHistoryDto

class SharedPreferencesSearchHistoryStorage(private val sharedPreferences: SharedPreferences): SearchHistoryStorage {

    private val trackList = mutableSetOf<TrackHistoryDto>()

    override fun getTracksHistory(): List<TrackHistoryDto> {
        setList()
        return trackList.toList()
    }

    override fun addTrack(track: TrackHistoryDto) {
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

    override fun clearHistory() {
        sharedPreferences.edit { putString(Const.LAST_SEARCH, "") }
    }

    private fun setList() {
        trackList.clear()
        try {
            trackList.addAll(sharedPreferences.getString(Const.LAST_SEARCH, "")?.deserializeToList(
                TrackHistoryDto::class.java)
                ?: emptyList())
        } catch (e: Throwable) {

        }

    }

}