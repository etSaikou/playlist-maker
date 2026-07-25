package com.saikou.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saikou.playlistmaker.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM track_table ORDER BY addedAt DESC")
    fun getTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM track_table")
    fun getTrackIds(): Flow<List<Long>>
}
