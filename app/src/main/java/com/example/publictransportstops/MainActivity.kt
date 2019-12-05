package com.example.publictransportstops

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

var stopsList = mutableListOf<Stop>()
var filteredStopsList = ArrayList<Stop>()
var isDataLoaded = false

class MainActivity : AppCompatActivity() {
    var searchQuery = "."
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                    val stop = Stop(id, name)
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
        myAdapter.notifyDataSetChanged()
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

        if (searchQuery != ".") {
            searchView.setQuery(searchQuery, true)
            searchView.isIconified = false
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


}
