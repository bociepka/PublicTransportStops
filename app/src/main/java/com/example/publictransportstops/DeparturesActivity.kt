package com.example.publictransportstops

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_departures.*
import org.json.JSONArray
import org.json.JSONObject

class DeparturesActivity : AppCompatActivity() {
    var departuresList = mutableListOf<Departure>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_departures)
        val id = intent.getIntExtra("id", 1)
        val stopName = intent.getStringExtra("name")
        title = stopName
        getDepartures(id)
    }


    private fun onDeparturesReady() {
        val departuresAdapter = DeparturesAdapter(departuresList as ArrayList<Departure>)
        departuresListView.adapter = departuresAdapter
        departuresAdapter.notifyDataSetChanged()
    }


    private fun getDepartures(id: Int) {
        departuresList.removeAll { true }
        val url = "https://krakowpodreka.pl/pl/stops/schedule/stop/$id/"

        val directionsRequest =
            object : StringRequest(Method.GET, url, Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                val departuresArray = jsonResponse.getJSONArray("future")
                for (i in 0..departuresArray.length() - 1) {
                    val jsonDeparture = departuresArray.getJSONObject(i)
                    val displayTime = jsonDeparture.get("display_time").toString()
                    val lineNumber = jsonDeparture.get("line_number").toString()
                    val direction = jsonDeparture.get("direction").toString()
                    val category = jsonDeparture.get("category").toString()
                    val delay = jsonDeparture.get("delay").toString()
                    val departure = Departure(displayTime, lineNumber, direction, category, delay)
                    departuresList.add(departure)
                }
                onDeparturesReady()
            }, Response.ErrorListener {
            }) {}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)
    }
}
