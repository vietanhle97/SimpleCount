package com.example.simplecount.ui.expense.expenseDisplay

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplecount.data.db.dao.EventDao
import com.example.simplecount.data.db.dao.ExpenseDao
import com.example.simplecount.data.db.entity.Event
import com.example.simplecount.data.db.entity.Expense
import kotlinx.coroutines.*

class ExpenseViewModel(
    val eventId : Long,
    val eventDao: EventDao,
    val expenseDao: ExpenseDao
) : ViewModel() {

    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _event = MutableLiveData<Event>()
    val event : LiveData<Event> get() = _event

    private val _update = MutableLiveData<Boolean>()
    val update : LiveData<Boolean> get() = _update

    val expenses = expenseDao.getAll(eventId)

    val currentEvent = eventDao.getEvent(eventId)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()

    }

    init {
        expenses
        _update.value = false
        getEvent(eventId)
    }

    fun getEvent(eventId: Long){
        uiScope.launch {
            getEventFromDatabase(eventId)
        }
    }

    private suspend fun getEventFromDatabase(eventId: Long){
        withContext(Dispatchers.IO){
            _event.postValue(eventDao.get(eventId))
        }
    }

    fun deleteAllExpenses(){
        uiScope.launch {
            deleteAllExpensesOfEvent()
        }
    }

    private suspend fun deleteAllExpensesOfEvent(){
        withContext(Dispatchers.IO){
            expenseDao.deleteAllDatabase()
        }
    }

    fun deleteExpense(expense : Expense){
        uiScope.launch {
            deleteExpenseFromDataBase(expense)
        }
    }

    private suspend fun deleteExpenseFromDataBase(expense: Expense){
        withContext(Dispatchers.IO){
            expenseDao.delete(expense)
        }
    }

    fun updateEvent(event: Event){
        uiScope.launch {
            updateEventToDataBase(event)
        }
    }

    private suspend fun updateEventToDataBase(event: Event){
        withContext(Dispatchers.IO){
            eventDao.update(event)
        }
    }

    fun insertExpense(expense: Expense){
        uiScope.launch {
            insertExpenseToDataBase(expense)
        }
    }

    private suspend fun insertExpenseToDataBase(expense: Expense){
        withContext(Dispatchers.IO){
            expenseDao.insert(expense)
        }
    }

    fun resetUpdate(){
        _update.value = false
    }

    fun setUpdate(){
        _update.value = true
    }

}