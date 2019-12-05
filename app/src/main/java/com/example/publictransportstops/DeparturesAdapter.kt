package com.example.publictransportstops

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class DeparturesAdapter (var objects: ArrayList<Departure>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View


        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(parent?.context)
            view = layoutInflater.inflate(R.layout.departure_item, parent, false)
        } else {
            view = convertView
        }
        view.findViewById<TextView>(R.id.lineNumber).text = "${objects.get(position).lineNumber} ${objects.get(position).direction}"
        view.findViewById<TextView>(R.id.departureTime).text = objects.get(position).displayTime

        view.setOnClickListener{

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