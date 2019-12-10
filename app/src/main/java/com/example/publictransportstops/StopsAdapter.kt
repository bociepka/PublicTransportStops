package com.example.publictransportstops

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView

class StopsAdapter (var objects: ArrayList<Stop>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View


        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(parent?.context)
            view = layoutInflater.inflate(R.layout.stop_item, parent, false)
        } else {
            view = convertView
        }
        view.findViewById<TextView>(R.id.stopName).text = objects.get(position).name
        val checkBox : CheckBox = view.findViewById<CheckBox>(R.id.favourite)
        checkBox.isChecked = objects.get(position).favourite
        checkBox.setOnClickListener{
            val index = stopsList.indexOf(objects[position])
            stopsList[index].favourite = checkBox.isChecked
            Log.i("TAG","${index.toString()} is now ${checkBox.isChecked}")
        }

        view.setOnClickListener{
            val context = parent?.context
            val intent = Intent(context,DeparturesActivity::class.java )
            intent.putExtra("id", objects[position].id)
            intent.putExtra("name",objects[position].name)
            context?.startActivity(intent)
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return objects[position]
    }

    override fun getItemId(position: Int): Long {
        return objects[position].hashCode().toLong()
    }

    override fun getCount(): Int {
        return objects.size
    }


}