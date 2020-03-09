package com.example.simplecount.data.db.converter

import androidx.room.TypeConverter
import com.example.simplecount.data.db.entity.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UserDataConverter {
    companion object {
        private var gson = Gson()
        @TypeConverter
        @JvmStatic
        fun stringToUserList(data: String): HashMap<Int, User> {
            if (data == null) {
                return hashMapOf()
            }

            val type = object : TypeToken<HashMap<Int,User>>() {}.type
            return gson.fromJson(data, type)
        }

        @TypeConverter
        @JvmStatic
        fun userListToString(users: HashMap<Int, User>): String {
            return gson.toJson(users)
        }
    }
}