package com.example.publictransportstops

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SimpleAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.*

var stopsList = mutableListOf<Stop>()
//var flag = false

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val thread = Thread(Runnable {
        getStops()
//        })
//        thread.start()
//        thread.join()
    }


    private fun onStopsReady(){

        correctNames()
//        Log.i("TAG",stopsList[1537].toString())
//        Log.i("TAG",stopsList[96].toString())
//        Log.i("TAG",stopsList[81].toString())
//        textview.text = stopsList[96].toString()

//        val intent = Intent(this, DeparturesActivity::class.java)
//        startActivity(intent)
        val myAdapter = StopsAdapter(stopsList as ArrayList<Stop>)
        listView.adapter = myAdapter
        myAdapter.notifyDataSetChanged()
    }

    private fun correctNames(){
        for(stop in stopsList){
            stop.name = stop.name.replace("Ä\u0084","Ą")
            stop.name = stop.name.replace("Ä\u0086","Ć")
            stop.name = stop.name.replace("Ä\u0098","Ę")
            stop.name = stop.name.replace("Å\u0081","Ł")
            stop.name = stop.name.replace("Å\u0083","Ń")
            stop.name = stop.name.replace("Ã\u0093","Ó")
            stop.name = stop.name.replace("Å\u009A","Ś")
            stop.name = stop.name.replace("Å\u00B9","Ź")
            stop.name = stop.name.replace("Å\u00BB","Ż")
            stop.name = stop.name.replace("Ä\u0085","ą")
            stop.name = stop.name.replace("Ä\u0087","ć")
            stop.name = stop.name.replace("Ä\u0099","ę")
            stop.name = stop.name.replace("Å\u0082","ł")
            stop.name = stop.name.replace("Å\u0084","ń")
            stop.name = stop.name.replace("Ã\u00B3","ó")
            stop.name = stop.name.replace("Å\u009B","ś")
            stop.name = stop.name.replace("Å\u00BA","ź")
            stop.name = stop.name.replace("Å\u00BC","ż")
            stop.name = stop.name.replace("Ã©","é")
        }
    }



    private fun getStops(){
        val url = "https://krakowpodreka.pl/pl/stops/positions/stops/?format=json"

            Log.i("TAG", "Helper thread started")

            val directionsRequest = object : StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
                val jsonResponse = JSONArray(response)
                for (i in 0..jsonResponse.length()-1) {
                    val currentStop = JSONObject(jsonResponse.get(i).toString())
                    val name = currentStop.get("name").toString()
                    val id = currentStop.get("id").toString().toInt()
                    val stop = Stop(id, name)
                    stopsList.add(stop)
                }
                onStopsReady()
            }, Response.ErrorListener {
            }){}
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(directionsRequest)
            Log.i("TAG","Helper thread ended")
    }
}
