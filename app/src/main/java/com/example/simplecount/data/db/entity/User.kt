package com.example.simplecount.data.db.entity

data class User(
    val id : Int,
    var name : String,
    var payAmount : Double,
    var participate : Boolean,
    var portion : Int
)