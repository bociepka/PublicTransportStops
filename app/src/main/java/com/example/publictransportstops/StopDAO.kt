package com.example.publictransportstops

import androidx.room.*


@Dao
interface StopDAO {
    @Query("SELECT * FROM stop")
    fun loadAllStops(): Array<Stop>

    @Query("SELECT * FROM stop WHERE id=:id")
    fun getStopById(id :Int):Stop

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStops(vararg stops: Stop)

    @Query("SELECT * FROM stop WHERE name LIKE :query")
    fun filterStops(query :String):Array<Stop>

//    @Delete("DELETE * FROM stop")
//    fun removeAll()
}