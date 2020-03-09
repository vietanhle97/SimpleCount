package com.example.simplecount.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.simplecount.data.db.converter.UserDataConverter
import com.example.simplecount.data.db.dao.ExpenseDao
import com.example.simplecount.data.db.entity.Expense

@Database(
    entities = [Expense :: class],
    version = 1,
    exportSchema = false
)
@TypeConverters(UserDataConverter::class)
abstract class ExpenseDatabase : RoomDatabase(){

    abstract val expenseDao : ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: ExpenseDatabase? = null

        fun geInstance(context: Context) : ExpenseDatabase{
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ExpenseDatabase::class.java,
                        "expense.db")
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}