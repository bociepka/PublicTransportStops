package com.example.publictransportstops

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

var stopsList = mutableListOf<Stop>()
var filteredStopsList = ArrayList<Stop>()
var isDataLoaded = false

class MainActivity : AppCompatActivity() {
    var searchQuery = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission()
        button.setOnClickListener {
            startMapActivity()
        }

        if (!isDataLoaded)
            getStops()
        else
            onStopsReady()

    }


    private fun onStopsReady() {
        correctNames()
        filteredStopsList = ArrayList(stopsList)
        var myAdapter = StopsAdapter(filteredStopsList)
        listView.adapter = myAdapter
        getFavourites()
        sortStopsList()
        myAdapter.notifyDataSetChanged()
    }

    private fun correctNames() {
        for (stop in stopsList) {
            stop.name = stop.name.replace("Ä\u0084", "Ą")
            stop.name = stop.name.replace("Ä\u0086", "Ć")
            stop.name = stop.name.replace("Ä\u0098", "Ę")
            stop.name = stop.name.replace("Å\u0081", "Ł")
            stop.name = stop.name.replace("Å\u0083", "Ń")
            stop.name = stop.name.replace("Ã\u0093", "Ó")
            stop.name = stop.name.replace("Å\u009A", "Ś")
            stop.name = stop.name.replace("Å\u00B9", "Ź")
            stop.name = stop.name.replace("Å\u00BB", "Ż")
            stop.name = stop.name.replace("Ä\u0085", "ą")
            stop.name = stop.name.replace("Ä\u0087", "ć")
            stop.name = stop.name.replace("Ä\u0099", "ę")
            stop.name = stop.name.replace("Å\u0082", "ł")
            stop.name = stop.name.replace("Å\u0084", "ń")
            stop.name = stop.name.replace("Ã\u00B3", "ó")
            stop.name = stop.name.replace("Å\u009B", "ś")
            stop.name = stop.name.replace("Å\u00BA", "ź")
            stop.name = stop.name.replace("Å\u00BC", "ż")
            stop.name = stop.name.replace("Ã©", "é")
        }
    }


    private fun getStops() {
        val url = "https://krakowpodreka.pl/pl/stops/positions/stops/?format=json"

        val directionsRequest =
            object : StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
                val jsonResponse = JSONArray(response)
                for (i in 0..jsonResponse.length() - 1) {
                    val currentStop = JSONObject(jsonResponse.get(i).toString())
                    val name = currentStop.get("name").toString()
                    val id = currentStop.get("id").toString().toInt()
                    val latitude = currentStop.get("latitude").toString().toDouble()
                    val longitude = currentStop.get("longitude").toString().toDouble()
                    val stop = Stop(id, name, latitude, longitude)
                    stopsList.add(stop)
                }
                isDataLoaded = true
                onStopsReady()
            }, Response.ErrorListener {
            }) {}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)
    }

    fun filterOutput(query: String) {
        filteredStopsList = ArrayList()
        for (stop in stopsList) {
            if (stop.name.toLowerCase().contains(query.toLowerCase())) {
                filteredStopsList.add(stop)
            }
        }
        var myAdapter = StopsAdapter(filteredStopsList)
        listView.adapter = myAdapter
        sortStopsList()
        myAdapter.notifyDataSetChanged()
    }

    fun sortStopsList(){
        val favouriteStops = ArrayList<Stop>()
        val nonFavouriteStops = ArrayList<Stop>()
        for (stop in filteredStopsList) {
            if (stop.favourite) {
                favouriteStops.add(stop)
            }
            else{
                nonFavouriteStops.add(stop)
            }
        }
        favouriteStops.sortBy{it.name}
        nonFavouriteStops.sortBy { it.name }
        filteredStopsList.removeAll { true }
        filteredStopsList.addAll(favouriteStops)
        filteredStopsList.addAll(nonFavouriteStops)
    }


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
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("search", searchQuery)
        super.onSaveInstanceState(outState)
        saveFavourites()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

        searchQuery = savedInstanceState.getString("search").toString()
        filterOutput(searchQuery)
        super.onRestoreInstanceState(savedInstanceState)
//        getFavourites()
    }


    fun saveFavourites(){
        val file : File = File(this.filesDir, "favourites.txt")
        file.writeText("")
        for ((index, stop) in stopsList.withIndex()){
            if(stop.favourite){
                file.appendText("$index,")
            }
        }
    }

    fun getFavourites() {
        val file : File = File(this.filesDir, "favourites.txt")
        var favourites: String = ""
        try {
            favourites = file.readText()
        }catch(e: Exception){

        }
        Log.i("TAG","String read")
        Log.i("TAG",favourites)
        var list : List<String> = favourites.split(',')
        Log.i("TAG","String splitted")
        for (favourite in list){
            Log.i("TAG","$favourite")
            if(favourite.toIntOrNull()!=null)
            stopsList[favourite.toInt()].favourite = true
            else{
                file.writeText("")
            }
        }
        Log.i("TAG","Favourites added")
//        Log.i("TAG",favourites)
    }
    /* MAP ACTIVITY */

    fun startMapActivity(){
        if(requestPermission()){
            val intent = Intent(this, MapsActivity::class.java)
            val bundle = Bundle()
            val tmp = ArrayList(stopsList)
            bundle.putParcelableArrayList("stops", tmp)
            intent.putExtras(bundle)
            startActivityForResult(intent,12)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("AAA","AAA")

        if(data==null)
            return

        val context = this
        val intent = Intent(context,DeparturesActivity::class.java)
        val name = data.getStringExtra("name")
        val id = data.getStringExtra("id")

        intent.putExtra("id", id?.toInt())
        intent.putExtra("name",name)

        startActivity(intent)
    }


    /* PERMISSIONS */

    private fun requestPermission():Boolean{
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions( this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1 )
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

        }else if(grantResults.isNotEmpty()){
            requestPermission()
        }
    }

}
