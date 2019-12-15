package com.example.publictransportstops

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf( Stop::class ), version = 1)
abstract  class LocalStopsDb : RoomDatabase() {
    abstract fun getStopsDAO(): StopDAO
}