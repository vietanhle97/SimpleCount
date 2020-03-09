package com.example.simplecount.ui.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplecount.data.db.dao.EventDao
import com.example.simplecount.data.db.dao.ExpenseDao

class HomeViewModelFactory (private val eventDao: EventDao, private val expenseDao: ExpenseDao, private val application: Application) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(eventDao, expenseDao, application) as T
        }
        throw IllegalArgumentException ("unknown ViewModel class")
    }
}