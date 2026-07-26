package com.saikou.playlistmaker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.saikou.playlistmaker.db.dao.PlaylistDao
import com.saikou.playlistmaker.db.dao.PlaylistTrackDao
import com.saikou.playlistmaker.db.dao.TrackDao
import com.saikou.playlistmaker.db.entity.PlaylistEntity
import com.saikou.playlistmaker.db.entity.PlaylistTrackEntity
import com.saikou.playlistmaker.db.entity.TrackEntity

@Database(version = 4, entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}
