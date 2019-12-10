package com.example.publictransportstops

import java.lang.Math.pow
import java.lang.Math.sqrt
import kotlin.math.pow

class Stop(var id: Int, var name: String, var latitude: Float, var longitude: Float) {
    var favourite: Boolean = false
    var distance: Double = 0.0

    override fun toString(): String {
        return "Stop nr $id - $name"
    }

    fun calculateDistance(otherLatitude: Float, otherLongitude: Float) {
        this.distance = kotlin.math.sqrt(
            (latitude - otherLatitude).toDouble().pow(2.0) + (longitude - otherLongitude).toDouble().pow(
                2.0
            )
        )
    }
}