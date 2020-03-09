package com.example.simplecount.ui.balance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplecount.data.db.dao.EventDao
import com.example.simplecount.data.db.dao.ExpenseDao
import com.example.simplecount.data.db.entity.Event
import kotlinx.coroutines.*

class BalanceViewModel (
    private val eventId : Long,
    private val expenseDao: ExpenseDao
) : ViewModel() {

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    val expenses = expenseDao.getAll(eventId)


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}