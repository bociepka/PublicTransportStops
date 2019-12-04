package com.example.publictransportstops

class Stop (var id: Int,var name: String){

    override fun toString() :String{
        return "Stop nr $id - $name"
    }
}