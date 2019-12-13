package com.example.publictransportstops

import android.app.ActionBar
import android.app.Activity
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

class Settings : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        setContentView(R.layout.activity_settings)

        actionBar?.setTitle(resources.getString(R.string.app_name))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        buttonChangeLanguage.setOnClickListener {
            showChangeLanguageDialog()
        }
    }

    private fun showChangeLanguageDialog() {
        lateinit var dialog:AlertDialog
        var listItems = arrayOf("English", "Deutsch", "Polski", "русский")
        var mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Choose language...")
        mBuilder.setSingleChoiceItems(listItems,-1,{_,which->
            val language = listItems[which]
            if (which==0){
                setLocale("en")
            } else if (which==1){
                setLocale("de")
            } else if (which==2){
                setLocale("pl")
            } else if (which==3){
                setLocale("ru")
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
        var editor = getSharedPreferences("Setting", Context.MODE_PRIVATE).edit()
        editor.putString("My lang", lang)
        editor.apply()
    }

    //load language
    public fun loadLocale(){
        var prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        var language = prefs.getString("My lang", "")
        setLocale(language!!)
    }
}
