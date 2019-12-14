package com.example.publictransportstops

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_departures.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class DeparturesActivity : AppCompatActivity() {
    var departuresList = mutableListOf<Departure>()
    var currentLangCode = String()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_departures, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.app_bar_settings){
            startSettings()
        }
        else if(item.itemId==R.id.app_bar_refresh){
            getDepartures(intent.getIntExtra("id", 1))
        }
        else if (item.itemId==android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_departures)

        currentLangCode = getResources().getConfiguration().locale.getLanguage()
        loadLocale()
        val id = intent.getIntExtra("id", 1)
        val stopName = intent.getStringExtra("name")
        title = stopName
        getDepartures(id)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        showWayButton.setOnClickListener {
            showWayToStop()
        }
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

    fun showWayToStop(){
        val stopIntent = Intent(this,MapsActivity::class.java)
        val latitude = intent.getDoubleExtra("lat",50.063511666)
        val longitude = intent.getDoubleExtra("lon",19.923723888)
        val stopName = intent.getStringExtra("name")


        stopIntent.putExtra("name",stopName)
        stopIntent.putExtra("lat",latitude)
        stopIntent.putExtra("lon",longitude)

        startActivity(stopIntent)

    }

    //load language
    fun loadLocale(){
        var prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        var language = prefs.getString("My lang", "")
        setLocale(language!!)
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
    override fun onResume() {
        super.onResume()
        if(!currentLangCode.equals(getResources().getConfiguration().locale.getLanguage())){
            currentLangCode = getResources().getConfiguration().locale.getLanguage()
            recreate()
        }
    }

    fun startSettings(){
        val intent = Intent(this, Settings::class.java)
        val bundle = Bundle()
        intent.putExtras(bundle)
        startActivityForResult(intent,12)
    }
}
