package com.example.simplecount.ui.expense.expenseCreation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplecount.data.db.dao.EventDao
import com.example.simplecount.data.db.dao.ExpenseDao

class ExpenseCreationViewModelFactory (private val eventId : Long, private val eventDao: EventDao, private val expenseDao: ExpenseDao) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseCreationViewModel::class.java)){
            return ExpenseCreationViewModel(eventId, eventDao, expenseDao) as T
        }

        throw IllegalArgumentException("unknown ViewModel Class")
    }

}