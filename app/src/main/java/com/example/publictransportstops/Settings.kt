package com.example.publictransportstops

import android.app.ActionBar
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*
import android.widget.Switch
import android.view.WindowManager
import android.widget.Toast
import java.io.File

class Settings : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home){
            finish()
//            recreate()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        setContentView(R.layout.activity_settings)
        setTitle(resources.getString(R.string.title_activity_settings))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        getFlag()
        buttonChangeLanguage.setOnClickListener {
            showChangeLanguageDialog()
        }
        var switchBigger = findViewById<Switch>(R.id.switchBigger)
        var switchColorblind = findViewById<Switch>(R.id.switchColorblind)
        var switchNight = findViewById<Switch>(R.id.switchNight)
        var editor = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        var night = editor.getString("Night", "false")
        var bigger = editor.getString("Bigger", "false")
        var colorblind = editor.getString("ColorBlind", "false")
        if (night=="true"){
            switchNight.setChecked(true)
        }
        if (bigger=="true"){
            switchBigger.setChecked(true)
        }
        if (colorblind=="true"){
            switchColorblind.setChecked(true)
        }
        switchBigger.setOnCheckedChangeListener { buttonView, isChecked ->
            var editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
            editor.putString("Bigger", isChecked.toString())
            editor.apply()
        }
        switchColorblind.setOnCheckedChangeListener { buttonView, isChecked ->
            var editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
            editor.putString("ColorBlind", isChecked.toString())
            editor.apply()
        }
        switchNight.setOnCheckedChangeListener { buttonView, isChecked ->
            var editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
            editor.putString("Night", isChecked.toString())
            editor.apply()
        }

        removeDataButton.setOnClickListener{
            val sharedPreferences = this.getSharedPreferences("SharedPref",Context.MODE_PRIVATE)
            sharedPreferences.edit().remove("sortingType").apply();
            File(this.filesDir, "favourites.txt").writeText("")
            for(stop in stopsList){
                stop.favourite = false
            }
            val text = R.string.data_removed
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
        }

        currentLangCode = getResources().getConfiguration().locale.getLanguage()
        currentLangCode += "2"
        setLocale(currentLangCode)
    }

    private fun showChangeLanguageDialog() {
        lateinit var dialog:AlertDialog
        var listItems = arrayOf("English", "Deutsch", "Polski", "русский", "Eesti keel")
        var mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Choose language...")
        mBuilder.setSingleChoiceItems(listItems, -1,
            DialogInterface.OnClickListener { dialog, item ->
                val language = listItems[item]
                if (item==0){
                    setLocale("en")
                    recreate()
                } else if (item==1){
                    setLocale("de")
                    recreate()
                } else if (item==2){
                    setLocale("pl")
                    recreate()
                } else if (item==3){
                    setLocale("ru")
                    recreate()
                } else if (item==4){
                    setLocale("et")
                    recreate()
                }
                dialog.dismiss()
            })
        dialog = mBuilder.create()
        dialog.show()
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

    //load language
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
            setTheme(R.style.AppThemeHighContrast)
        } else if (colorblind=="true"){
            setTheme(R.style.AppThemeHighContrast)
        } else if (night=="true"){
            setTheme(R.style.AppThemeNight)
        } else {
            setTheme(R.style.AppTheme)
        }
    }

    fun getFlag(){
        var prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        var language = prefs.getString("My lang", "")
        var flagPlace = languageFlag
        if (language=="en"){
            flagPlace.setImageDrawable(getDrawable(R.drawable.en))
        } else if (language=="de"){
            flagPlace.setImageDrawable(getDrawable(R.drawable.de))
        } else if (language=="ru"){
            flagPlace.setImageDrawable(getDrawable(R.drawable.ru))
        } else if (language=="pl"){
            flagPlace.setImageDrawable(getDrawable(R.drawable.pl))
        } else if (language=="et") {
            flagPlace.setImageDrawable(getDrawable(R.drawable.ee))
        }
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
