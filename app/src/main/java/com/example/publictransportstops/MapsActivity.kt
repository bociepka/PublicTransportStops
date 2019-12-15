package com.example.publictransportstops

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import org.json.JSONObject

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var currentLangCode = String()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_maps, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.app_bar_settings){
            startSettings()
        } else if (item.itemId == R.id.home){
            finish()
        }
        else if (item.itemId==android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        currentLangCode = getResources().getConfiguration().locale.getLanguage();
        setTitle(resources.getString(R.string.title_activity_maps))  //reloading the title to language

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
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
        var prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        var night = prefs.getString("Night", "false")
        if (night=="true") {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps))
        }
        val bundle = intent.extras
        val stops: ArrayList<Stop>? = bundle?.getParcelableArrayList("stops")

        try {
            val location = getCurrentLocation()
            val currentLocation = LatLng(location.latitude, location.longitude)

            if(stops!=null) {
                for (i in stops) {
                    i.calculateDistance(location.latitude,location.longitude)
                    mMap.addMarker(MarkerOptions().position(LatLng(i.latitude,i.longitude)).title(i.name + "," + i.id))
                    mMap.setOnMarkerClickListener{
                            marker -> onMarkerClick(marker)
                    }
                }
            }
            else{
                val name = intent.getStringExtra("name")
                val lat = intent.getDoubleExtra("lat",50.063511666)
                val lon = intent.getDoubleExtra("lon",19.923723888)

                Log.i("NAME",name)
                Log.i("LATITUDE", lat.toString())
                Log.i("LONGITUDE",lon.toString())

                mMap.addMarker(MarkerOptions().position(LatLng(lat, lon)).title(name))
                val url = createURL(currentLocation,"walking",lat,lon)
                val result = sendRequest(url)
            }
            mMap.addMarker(MarkerOptions().position(currentLocation).title("You are here")) // TODO set icon

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15F))
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

        if(split.size==2) {
            resIntent.putExtra("name", split[0])
            resIntent.putExtra("id", split[1])
        }
        resIntent.putExtra("lat",marker.position.latitude)
        resIntent.putExtra("lon",marker.position.longitude)

        setResult(12,resIntent)
        finish()

        return true
    }

    private fun createURL(origin: LatLng, directionMode: String, lat: Double, lon: Double): String{
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDestin = "destination=${lat},${lon}"
        val mode = "mode=$directionMode"
        val parameters = "$strOrigin&$strDestin&$mode"
        val outputFormat = "json"
        val url = "https://maps.googleapis.com/maps/api/directions/$outputFormat?$parameters&key=${resources.getString(R.string.google_maps_key)}"
        return url
    }

    private fun sendRequest(url: String){
        val directionRequest = object : StringRequest(
            Request.Method.GET,
            url,
            Response.Listener<String> {
                    response ->
                val path: MutableList<List<LatLng>> = ArrayList()
                val jsonResponse =  JSONObject(response)

                //Log.i("TEST",jsonResponse.toString())
                try {
                    val routes = jsonResponse.getJSONArray("routes")
                    val legs = routes.getJSONObject(0).getJSONArray("legs")
                    val steps = legs.getJSONObject(0).getJSONArray("steps")

                    for (i in 0 until steps.length()) {
                        val points =
                            steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                        path.add(PolyUtil.decode(points))
                    }

                    for (i in 0 until path.size) {
                        mMap.addPolyline(PolylineOptions().addAll(path[i]).color(Color.BLUE))
                    }
                }catch (e: Exception){
                    Log.e("Error","It is impossible to reach hardcoded location from your position")
                }
            },
            Response.ErrorListener {}
        ){}

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionRequest)
    }

    fun startSettings(){
        val intent = Intent(this, Settings::class.java)
        val bundle = Bundle()
        intent.putExtras(bundle)
        startActivityForResult(intent,12)
    }

    override fun onResume() {
        super.onResume()
        if(!currentLangCode.equals(getResources().getConfiguration().locale.getLanguage())){
            currentLangCode = getResources().getConfiguration().locale.getLanguage()
            recreate()
        }
    }
}
