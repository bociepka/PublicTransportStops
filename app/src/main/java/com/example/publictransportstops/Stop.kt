package com.example.publictransportstops

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.Math.pow
import java.lang.Math.sqrt
import kotlin.math.pow

@Entity(tableName = "Stop")
class Stop(@PrimaryKey(autoGenerate = false)var id: Int, var name: String, var latitude: Double, var longitude: Double) : Parcelable {
    var favourite: Boolean = false
    var distance: Double = 0.0

    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        name = parcel.readString()!!,
        latitude = parcel.readDouble(),
        longitude = parcel.readDouble()
    ) {
        favourite = parcel.readByte() != 0.toByte()
        distance = parcel.readDouble()
    }

    override fun toString(): String {
        return "Stop nr $id - $name"
    }

    fun calculateDistance(otherLatitude: Double, otherLongitude: Double) {
        this.distance = kotlin.math.sqrt(
            (latitude - otherLatitude).pow(2.0) + (longitude - otherLongitude).pow(2.0)
        )
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeByte(if (favourite) 1 else 0)
        parcel.writeDouble(distance)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Stop> {
        override fun createFromParcel(parcel: Parcel): Stop {
            return Stop(parcel)
        }

        override fun newArray(size: Int): Array<Stop?> {
            return arrayOfNulls(size)
        }
    }
}