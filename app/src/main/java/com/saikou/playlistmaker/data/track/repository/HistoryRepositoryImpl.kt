package com.saikou.playlistmaker.data.track.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.saikou.playlistmaker.data.track.entity.Track
import com.saikou.playlistmaker.domain.api.HistoryRepository
import com.saikou.playlistmaker.global.Const
import com.saikou.playlistmaker.global.deserializeToList
import com.saikou.playlistmaker.global.reAdd
import com.saikou.playlistmaker.global.removeFirst
import com.saikou.playlistmaker.global.serialize

class HistoryRepositoryImpl(private val context: Context): HistoryRepository {
    private val trackList = mutableSetOf<Track>()
    private val sharedPreferences = context.getSharedPreferences(
        Const.SHARED_PREFS,
        MODE_PRIVATE
    )

    override fun getTracksHistory(): List<Track> {
        setList()
        return trackList.toList()
    }

    override fun addTrack(track: Track) {
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

    override fun clearHistory() {
        sharedPreferences.edit { putString(Const.LAST_SEARCH, "") }
    }

    private fun setList() {
        trackList.clear()
        try {
            trackList.addAll(sharedPreferences.getString(Const.LAST_SEARCH, "")?.deserializeToList(Track::class.java)
                ?: emptyList())
        } catch (e: Throwable) {
            Log.e("SOMEZ", e.toString())
        }

    }

}