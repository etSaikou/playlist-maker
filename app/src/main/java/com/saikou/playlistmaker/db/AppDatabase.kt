package com.saikou.playlistmaker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.saikou.playlistmaker.db.dao.TrackDao
import com.saikou.playlistmaker.db.entity.TrackEntity

@Database(version = 2, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
}
