package com.example.simplecount.ui.expense.expenseDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplecount.data.db.dao.ExpenseDao

class ExpenseDetailViewModelFactory (private val expenseId : Long, private val expenseDao: ExpenseDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseDetailViewModel::class.java)){
            return ExpenseDetailViewModel(expenseId, expenseDao) as T
        }

        throw IllegalArgumentException ("cannot find ViewModel class")
    }
}