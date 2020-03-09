package com.example.simplecount.ui.expense.expenseDisplay

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplecount.data.db.dao.EventDao
import com.example.simplecount.data.db.dao.ExpenseDao

class ExpenseViewModelFactory (private val eventId : Long, private val eventDao: EventDao, private val expenseDao: ExpenseDao) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel:: class.java)) {
            return ExpenseViewModel(
                eventId,
                eventDao,
                expenseDao
            ) as T
        }
        throw IllegalArgumentException ("unknown ViewModel class")
    }

}