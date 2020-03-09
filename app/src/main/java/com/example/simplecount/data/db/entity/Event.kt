package com.example.simplecount.data.db.entity

import androidx.room.*
import com.example.simplecount.data.db.converter.UserDataConverter

@Entity(tableName = "event_table")
data class Event(

    @PrimaryKey(autoGenerate = true)
    val id : Long = 0L,

    @ColumnInfo(name = "title")
    var title : String = "",

    @ColumnInfo(name = "description")
    var description : String = "",

    @ColumnInfo(name = "date")
    var date : String = "",

    @ColumnInfo(name = "currency")
    var currency : String = "",

    @ColumnInfo(name = "participants")
    @TypeConverters(UserDataConverter::class)
    var participants : HashMap<Int, User> = hashMapOf(),

    @ColumnInfo(name = "amount")
    var amount : Double = 0.0
    ){

    fun getUser(userId : Int) : User {
        return participants[userId]!!
    }
}