package com.example.simplecount.data.db

import android.content.Context
import androidx.room.*
import com.example.simplecount.data.db.converter.UserDataConverter
import com.example.simplecount.data.db.dao.EventDao
import com.example.simplecount.data.db.entity.Event

@Database(
    entities = [Event :: class],
    version = 1,
    exportSchema = false
)
@TypeConverters(UserDataConverter::class)
abstract class EventDatabase : RoomDatabase(){

    abstract val eventDao : EventDao

    companion object {
        @Volatile
        private var INSTANCE: EventDatabase? = null

        fun geInstance(context: Context) : EventDatabase{
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        EventDatabase::class.java,
                        "event.db")
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}