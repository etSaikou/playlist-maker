package com.saikou.playlistmaker.global

import android.content.SharedPreferences
import android.util.Log
import com.saikou.playlistmaker.entity.Track
import androidx.core.content.edit

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    private val trackList = mutableSetOf<Track>()

    fun addToHistory(track: Track) {
        setList()
        if (trackList.size == 10) {
            trackList.removeFirst()
            Log.e("SOMEZ", "Десятка")
        }
        if (trackList.contains(track)){
            trackList.reAdd(track)
        }
        trackList.add(track)
        Log.e("SOMEZ", "Добавлен")

        sharedPreferences.edit {
            putString(Const.LAST_SEARCH, trackList.serialize())
        }.also {
            Log.e("SOMEZ", "Префы?!")
        }
    }

    fun getList(): List<Track> {
        setList()
        return trackList.toList()
    }

    fun clearHistory() {
        sharedPreferences.edit { putString(Const.LAST_SEARCH, "") }
    }

    private fun setList() {
        trackList.clear()
        try {
            trackList.addAll(sharedPreferences.getString(Const.LAST_SEARCH, "")?.deserializeToList(Track::class.java)
                ?: listOf())
        } catch (e: Throwable) {
            Log.e("SOMEZ", e.toString())
        }

    }
}