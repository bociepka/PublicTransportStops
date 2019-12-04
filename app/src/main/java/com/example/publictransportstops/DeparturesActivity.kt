package com.example.publictransportstops

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class DeparturesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_departures)
        var id = 1
        getDepartures(id)
    }




    private fun onDeparturesReady(){
        Log.i("TAG","Done")
    }




    private fun getDepartures(id:Int){
        val url = "https://krakowpodreka.pl/pl/stops/schedule/stop/$id/"

        Log.i("TAG", "Helper thread started")

        val directionsRequest = object : StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
            val jsonResponse = JSONObject(response)
            val departuresArray = jsonResponse.getJSONArray("future")
            for (i in 0..departuresArray.length()-1) {
                Log.i("TAG", departuresArray.get(i).toString())
            }
            onDeparturesReady()
        }, Response.ErrorListener {
        }){}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)
        Log.i("TAG","Helper thread ended")
    }
}
