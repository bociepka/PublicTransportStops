package com.example.publictransportstops

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface StopDAO {
    @Query("SELECT * FROM stop")
    fun loadAllStops(): Array<Stop>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStops(vararg recipes: Stop)
}