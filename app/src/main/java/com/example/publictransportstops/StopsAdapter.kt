package com.example.publictransportstops

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
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

        view.setOnClickListener{
            val context = parent?.context
            val intent = Intent(context,DeparturesActivity::class.java )
            intent.putExtra("id",objects.get(position).id)
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