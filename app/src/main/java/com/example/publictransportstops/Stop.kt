package com.example.publictransportstops

class Stop (var id: Int,var name: String, var favourite: Boolean){

    override fun toString() :String{
        return "Stop nr $id - $name"
    }
}