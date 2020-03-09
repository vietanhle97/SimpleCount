package com.example.simplecount.ui.expense.expenseCreation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplecount.data.db.dao.EventDao
import com.example.simplecount.data.db.dao.ExpenseDao
import com.example.simplecount.data.db.entity.Event
import com.example.simplecount.data.db.entity.Expense
import com.example.simplecount.data.db.entity.User
import kotlinx.coroutines.*

class ExpenseCreationViewModel(
    val eventId : Long,
    private val eventDao: EventDao,
    private val expenseDao: ExpenseDao

) : ViewModel() {


    val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _currentEvent = MutableLiveData<Event>()

    private val _idExpense = MutableLiveData<Long>()
    val idExpense : LiveData<Long> get() = _idExpense

    val currentEvent : LiveData<Event> get() = _currentEvent

    init {
        getEvent(eventId)
    }

    private fun getEvent(id : Long){
        uiScope.launch {
            getEventFromDatabase(id)
        }
    }

    private suspend fun getEventFromDatabase(id: Long){
        withContext(Dispatchers.IO){
            _currentEvent.postValue(eventDao.get(id))
        }
    }


    fun insertExpense(expense: Expense){
        uiScope.launch {
            _idExpense.postValue(insertExpenseToDatabse(expense))
        }
    }

    private suspend fun insertExpenseToDatabse(expense: Expense) : Long{
        return withContext(Dispatchers.IO){
           expenseDao.insert(expense)
        }
    }

    fun updateEvent(event: Event){
        uiScope.launch {
            updateEventToDatabase(event)
        }
    }

    private suspend fun updateEventToDatabase(event: Event){
        withContext(Dispatchers.IO){
            eventDao.update(event)
        }
    }

    fun resetIdExpense(){
        _idExpense.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}