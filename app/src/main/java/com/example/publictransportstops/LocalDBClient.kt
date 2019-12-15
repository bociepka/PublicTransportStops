package com.example.publictransportstops

import android.content.Context
import androidx.room.Room

object LocalDbClient {
    var stopDB : LocalStopsDb? = null
    fun getDatabase(context: Context) : LocalStopsDb? {

        if (stopDB == null){
            stopDB = Room.databaseBuilder(
                context, LocalStopsDb::class.java, "stops")
                .fallbackToDestructiveMigration() // each time schema changes, data is lost!
                .allowMainThreadQueries() // if possible, use background thread instead
                .build()
        }
        return stopDB
    }

}