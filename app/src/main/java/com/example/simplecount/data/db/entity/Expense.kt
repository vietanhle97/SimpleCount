package com.example.simplecount.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.simplecount.data.db.converter.UserDataConverter
import java.util.*
import kotlin.collections.HashMap

@Entity(tableName = "expense_table")
data class Expense(

    @PrimaryKey(autoGenerate = true)
    var id : Long = 0L,

    @ColumnInfo(name = "eventId")
    var eventId : Long = 0L,

    @ColumnInfo(name = "title")
    var title : String = "",

    @ColumnInfo(name = "date")
    var date : String = "",

    @ColumnInfo(name = "currency")
    var currency: String = "",

    @ColumnInfo(name = "participants")
    @TypeConverters(UserDataConverter::class)
    var participants : HashMap<Int, User> = hashMapOf(),

    @ColumnInfo(name = "payer")
    var payer : Int = 0,

    @ColumnInfo(name = "amount")
    var amount : Double = 0.0
)