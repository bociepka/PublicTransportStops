package com.example.publictransportstops

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import com.example.publictransportstops.Settings as Settings1

//var stopsList = ArrayList<Stop>()
var filteredStopsList = ArrayList<Stop>()
//var isDataLoaded = false
var currentLangCode = String()


class MainActivity : AppCompatActivity() {
    lateinit var db : LocalStopsDb
    var sortingType = "location"
    var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = LocalDbClient.getDatabase(this)!!
        val sharedPref = this.getSharedPreferences("SharedPref",Context.MODE_PRIVATE)
        sortingType = sharedPref.getString("sortingType", "name") as String
        loadLocale()
        currentLangCode = getResources().getConfiguration().locale.getLanguage()
        setTitle(resources.getString(R.string.app_name))  //reloading the title to language
        setContentView(R.layout.activity_main)

        if(requestPermission()){
            main()
        }


    }


    fun main(){
        mapButton.setOnClickListener {
            startMapActivity()
        }

        if (db.getStopsDAO().loadAllStops().isEmpty())
            getStops()
        else
            onStopsReady()
    }


    private fun onStopsReady() {
        filteredStopsList.removeAll{true}
//        stopsList.removeAll{true}
        for(i in db!!.getStopsDAO().loadAllStops()){
            filteredStopsList.add(i)
        }
//        filteredStopsList = ArrayList(stopsList)
        val myAdapter = StopsAdapter(filteredStopsList)
        listView.adapter = myAdapter
        sortStopsList(sortingType)
        myAdapter.notifyDataSetChanged()
    }

    private fun correctName(name :String) : String{
        return name.replace("Ä\u0084", "Ą")
            .replace("Ä\u0086", "Ć")
            .replace("Ä\u0098", "Ę")
            .replace("Å\u0081", "Ł")
            .replace("Å\u0083", "Ń")
            .replace("Ã\u0093", "Ó")
            .replace("Å\u009A", "Ś")
            .replace("Å\u00B9", "Ź")
            .replace("Å\u00BB", "Ż")
            .replace("Ä\u0085", "ą")
            .replace("Ä\u0087", "ć")
            .replace("Ä\u0099", "ę")
            .replace("Å\u0082", "ł")
            .replace("Å\u0084", "ń")
            .replace("Ã\u00B3", "ó")
            .replace("Å\u009B", "ś")
            .replace("Å\u00BA", "ź")
            .replace("Å\u00BC", "ż")
            .replace("Ã©", "é")
    }


    private fun getStops() {
        Log.i("TAG","started request")
        val url = "https://krakowpodreka.pl/pl/stops/positions/stops/?format=json"
        val directionsRequest =
            object : StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
                val jsonResponse = JSONArray(response)
                for (i in 0..jsonResponse.length() - 1) {
                    val currentStop = JSONObject(jsonResponse.get(i).toString())
                    val name = correctName(currentStop.get("name").toString())
                    val id = currentStop.get("id").toString().toInt()
                    val latitude = currentStop.get("latitude").toString().toDouble()
                    val longitude = currentStop.get("longitude").toString().toDouble()
                    val stop = Stop(id, name, latitude, longitude)
                    db.getStopsDAO().insertStops(stop)
                }
//                isDataLoaded = true
                onStopsReady()
            }, Response.ErrorListener {
            }) {}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)
    }

    fun filterOutput(query: String) {
        filteredStopsList = ArrayList()

        for(i in db.getStopsDAO().filterStops("%$query%")){
            filteredStopsList.add(i)
        }
        var myAdapter = StopsAdapter(filteredStopsList)
        listView.adapter = myAdapter
        sortStopsList(sortingType)
        myAdapter.notifyDataSetChanged()
    }

    fun sortStopsList(type : String){
        val favouriteStops = ArrayList<Stop>()
        val nonFavouriteStops = ArrayList<Stop>()
        val myLocation = getCurrentLocation()
        Log.i("Location","${myLocation?.latitude} ${myLocation?.longitude}")
        for (stop in filteredStopsList) {
            if (myLocation != null){
                stop.calculateDistance(myLocation.latitude,myLocation.longitude)
            }
            if (stop.favourite) {
                favouriteStops.add(stop)
            }
            else{
                nonFavouriteStops.add(stop)
            }
        }

        if (type == "location"){

            favouriteStops.sortBy { it.distance }
            nonFavouriteStops.sortBy { it.distance }
        }
        else {
            favouriteStops.sortBy { it.name }
            nonFavouriteStops.sortBy { it.name }
        }
        filteredStopsList.removeAll { true }
        filteredStopsList.addAll(favouriteStops)
        filteredStopsList.addAll(nonFavouriteStops)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean { // Handle item selection
        if (item.itemId == R.id.app_bar_settings){
            startSettings()
        }
        else if(item.itemId == R.id.app_bar_sort_distance){
            sortingType = "location"
//            sortStopsList(sortingType)
//            val myAdapter = StopsAdapter(filteredStopsList)
//            listView.adapter = myAdapter
//            myAdapter.notifyDataSetChanged()
            val sharedPref = this.getSharedPreferences("SharedPref",Context.MODE_PRIVATE)
            with(sharedPref.edit()){
                putString("sortingType", sortingType)
                apply()
            }
            onStopsReady()
        }
        else if(item.itemId == R.id.app_bar_sort_name){
            sortingType = "name"
//            sortStopsList(sortingType)
//            val myAdapter = StopsAdapter(filteredStopsList)
//            listView.adapter = myAdapter
//            myAdapter.notifyDataSetChanged()
            val sharedPref = this.getSharedPreferences("SharedPref",Context.MODE_PRIVATE)
            with(sharedPref.edit()){
                putString("sortingType", sortingType)
                apply()
            }
            onStopsReady()
        }

        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem: MenuItem = menu!!.findItem(R.id.app_bar_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                searchQuery = newText
                filterOutput(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                filterOutput(query)
                return false
            }
        })

        if (searchQuery != "") {
            searchView.setQuery(searchQuery, true)
            searchView.isIconified = false
        }
        //adjusting the motiv
        var prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        var night = prefs.getString("Night", "false")
        var colorblind = prefs.getString("ColorBlind", "false")
        var bar = supportActionBar
        if (colorblind=="true"){
            var color = ColorDrawable(getColor(R.color.red))
            bar!!.setBackgroundDrawable(color)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("search", searchQuery)
        super.onSaveInstanceState(outState)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        searchQuery = savedInstanceState.getString("search").toString()
        filterOutput(searchQuery)
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        loadLocale()
        var tempLangCode = getResources().getConfiguration().locale.getLanguage()
        if (db.getStopsDAO().loadAllStops().isEmpty()){
            filteredStopsList.removeAll{true}
            var myAdapter = StopsAdapter(filteredStopsList)
            listView.adapter = myAdapter
            myAdapter.notifyDataSetChanged()
            getStops()
        }
        else if (tempLangCode.contains('2')){
            var newTempLang = tempLangCode.dropLast(1)
            setLocale(newTempLang)
            recreate()
        }
        else if(!currentLangCode.equals(getResources().getConfiguration().locale.getLanguage())){
            currentLangCode = getResources().getConfiguration().locale.getLanguage()
            recreate()
        }
    }



    /* MAP ACTIVITY */

    fun startMapActivity(){

        if(requestPermission()){
            val intent = Intent(this, MapsActivity::class.java)
            startActivityForResult(intent,12)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode!=12)
            onResume()
        else {
            Log.i("chuj", "chuj")
            if (data == null)
                return

            val context = this
            val intent = Intent(context, DeparturesActivity::class.java)
            val id = data.getStringExtra("id")
            val name = data.getStringExtra("name")
            val latitude = data.getDoubleExtra("lat", 50.063511666)
            val longitude = data.getDoubleExtra("lon", 19.923723888)

            intent.putExtra("id", id?.toInt())
            intent.putExtra("name", name)
            intent.putExtra("lat", latitude)
            intent.putExtra("lon", longitude)

            startActivity(intent)
        }
    }


    /* PERMISSIONS */

    private fun requestPermission():Boolean{
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions( this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1 )
            ActivityCompat.requestPermissions( this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1 )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            main()

        }else if(grantResults.isNotEmpty()){
            requestPermission()
        }
    }

    /* SETTINGS */

    fun startSettings(){
        val intent = Intent(this, Settings1::class.java)
        startActivityForResult(intent,14)
    }
    private fun getCurrentLocation(): Location? {
        val trackLocation = TrackLocation(this)
        if(trackLocation.canGetLocation)
            return trackLocation.location
        else{
            return null
        }
    }
    //load language and adjust motive
    fun loadLocale(){
        var prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        var language = prefs.getString("My lang", "")
        setLocale(language!!)
        var bigger = prefs.getString("Bigger", "false")
        if (bigger=="true"){
            adjustFontScale(resources.configuration, 2.5f)
        } else {
            adjustFontScale(resources.configuration, 1.5f)
        }
        var colorblind = prefs.getString("ColorBlind", "false")
        var night = prefs.getString("Night", "false")
        if (colorblind=="true" && night=="true"){
            this.setTheme(R.style.AppThemeHighContrast)
        } else if (colorblind=="true"){
            this.setTheme(R.style.AppThemeHighContrast)
        } else if (night=="true"){
            this.setTheme(R.style.AppThemeNight)
        } else {
            this.setTheme(R.style.AppTheme)
        }
    }
    private fun setLocale(lang: String) {
        var locale = Locale(lang)
        Locale.setDefault(locale)
        var config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        var editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My lang", lang)
        editor.apply()
    }
    fun adjustFontScale(configuration: Configuration, scale: Float) {
        configuration.fontScale = scale
        val metrics = resources.displayMetrics
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(configuration, metrics)
    }
}
