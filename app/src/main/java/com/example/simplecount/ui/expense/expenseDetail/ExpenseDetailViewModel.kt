package com.example.simplecount.ui.expense.expenseDetail

import androidx.lifecycle.ViewModel
import com.example.simplecount.data.db.dao.ExpenseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class ExpenseDetailViewModel (
    val expenseId : Long,
    val expenseDao: ExpenseDao
) : ViewModel(){

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val expense = expenseDao.get(expenseId)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}