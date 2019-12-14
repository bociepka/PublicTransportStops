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
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils

import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*
import android.widget.Toast
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



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
    }

    private fun showChangeLanguageDialog() {
        lateinit var dialog:AlertDialog
        var listItems = arrayOf("English", "Deutsch", "Polski", "русский")
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
        Log.i("jezyk", "ustawiono" + lang)
        editor.putString("My lang", lang)
        editor.apply()
    }

    //load language
    fun loadLocale(){
        var prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        var language = prefs.getString("My lang", "")
        setLocale(language!!)
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
        } else if (language=="ee") {
            flagPlace.setImageDrawable(getDrawable(R.drawable.ee))
        }
    }
}
