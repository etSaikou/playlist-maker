package com.saikou.playlistmaker.data.track.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.saikou.playlistmaker.data.track.entity.Track
import com.saikou.playlistmaker.data.track.entity.TrackHistoryDto
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
        }
        if (trackList.contains(track)){
            trackList.reAdd(track)
        }
        trackList.add(track)


        sharedPreferences.edit {
            putString(Const.LAST_SEARCH, trackList.map {
                TrackHistoryDto(it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.trackId,
                    it.collectionName,
                    it.releaseDate?:"",
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl)
            }.serialize())
        }
    }

    override fun clearHistory() {
        sharedPreferences.edit { putString(Const.LAST_SEARCH, "") }
    }

    private fun setList() {
        trackList.clear()
        try {
            trackList.addAll(sharedPreferences.getString(Const.LAST_SEARCH, "")?.deserializeToList(
                TrackHistoryDto::class.java)?.map {
                    Track(it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.trackId,
                        it.collectionName,
                        it.releaseDate?:"",
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl)
            }
                ?: emptyList())
        } catch (e: Throwable) {

        }

    }

}