package com.example.simplecount.ui.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplecount.data.db.dao.EventDao
import com.example.simplecount.data.db.dao.ExpenseDao

class BalanceViewModelFactory (private val eventId : Long, private val expenseDao: ExpenseDao) : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BalanceViewModel::class.java)){
            return BalanceViewModel(eventId, expenseDao) as T
        }
        throw IllegalArgumentException("cannot find ViewModel class")

    }
}