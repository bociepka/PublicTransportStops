package com.example.publictransportstops

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        createMapAndMarkMyLocation()
    }


    private fun createMapAndMarkMyLocation(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getCurrentLocation(): Location {
        val trackLocation = TrackLocation(this)
        if(trackLocation.canGetLocation)
            return trackLocation.location
        else{
            throw Exception()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val bundle = intent.extras
        val stops: ArrayList<Stop>? = bundle?.getParcelableArrayList("stops")

        try {
            val location = getCurrentLocation()
            val currentLocation = LatLng(location.latitude, location.longitude)

            if(stops!=null) {
                for (i in stops) {
                    i.calculateDistance(location.latitude,location.longitude)
                    mMap.addMarker(MarkerOptions().position(LatLng(i.latitude,i.longitude)).title(i.name + "," + i.id))
                }
            }
            else{
                setMarkerOnStop(mMap)
            }
            mMap.addMarker(MarkerOptions().position(currentLocation).title("You are here")) // TODO set icon

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15F))

            mMap.setOnMarkerClickListener{
                marker -> onMarkerClick(marker)
            }
        }
        catch (e: Exception){
            Log.e("Location error","It is impossible to get location using GPS")
        }
    }

    fun onMarkerClick(marker: Marker?): Boolean {
        if(marker==null)
            return true
        val resIntent = Intent()
        val split = marker.title.split(",")

        resIntent.putExtra("name",split[0])
        resIntent.putExtra("id",split[1])
        resIntent.putExtra("latitude",marker.position.latitude)
        resIntent.putExtra("longitude",marker.position.longitude)

        setResult(12,resIntent)
        finish()
        return true
    }

    fun setMarkerOnStop(mMap: GoogleMap){
        val name = intent.getStringExtra("name")
        val lat = intent.getStringExtra("latitude")
        val long = intent.getStringExtra("longitude")

        Log.i("NAME",name)
        Log.i("LATITUDE", lat)
        Log.i("LONGITUDE",long)

        if(lat!=null && long != null)
            mMap.addMarker(MarkerOptions().position(LatLng(lat.toDouble(),long.toDouble())).title(name))
    }

}
