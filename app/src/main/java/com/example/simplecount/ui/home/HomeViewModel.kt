package com.example.simplecount.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.simplecount.data.db.dao.EventDao
import com.example.simplecount.data.db.dao.ExpenseDao
import com.example.simplecount.data.db.entity.Event
import com.example.simplecount.data.db.entity.Expense
import kotlinx.coroutines.*

class HomeViewModel(
    val eventDao: EventDao,
    val expenseDao: ExpenseDao,
    application: Application)
    : AndroidViewModel(application){

    private var eventViewModelJob = Job()
    private val uiScope = CoroutineScope(eventViewModelJob + Dispatchers.Main)

    val events = eventDao.getAll()

    private val _deleteEventId = MutableLiveData<Long>()

    val deleteEventId : LiveData<Long> get() = _deleteEventId

    private val _undo = MutableLiveData<Boolean>()
    val undo : LiveData<Boolean> get() = _undo

    private val _idEvent = MutableLiveData<Long>()
    val idEvent : LiveData<Long> get() = _idEvent


    init {
        _deleteEventId.value = null
        _undo.value = false
    }

    override fun onCleared() {
        super.onCleared()
        eventViewModelJob.cancel()
        Log.e("homeViewModel", "cancel")
    }

    fun deleteEvent(deleteEvent: Event){
        uiScope.launch {
            deleteEventInDatabase(deleteEvent)
        }
    }

    private suspend fun deleteEventInDatabase(deleteEvent : Event){
        withContext(Dispatchers.IO){
            eventDao.delete(deleteEvent)
        }
    }

    fun deleteAllEvents(){
        uiScope.launch {
            deleteAllEventsInDatabase()
        }
    }

    private suspend fun deleteAllEventsInDatabase(){
        withContext(Dispatchers.IO){
            eventDao.deleteAll()
        }
    }

    fun deleteAllExpense(){
        uiScope.launch {
            deleteAllExpenseInDataBase()
        }
    }

    private suspend fun deleteAllExpenseInDataBase(){
        withContext(Dispatchers.IO){
            expenseDao.deleteAllDatabase()
        }
    }

    fun deleteAllExpenseOfEvent(eventId: Long){
        uiScope.launch {
            deleteAllExpenseOfEventInDataBase(eventId)
        }
    }

    private suspend fun deleteAllExpenseOfEventInDataBase(eventId: Long){
        withContext(Dispatchers.IO){
            expenseDao.deleteAll(eventId)
        }
    }

    fun insert(event: Event){
        uiScope.launch {
            insertEventToDataBase(event)
        }
    }

    private suspend fun insertEventToDataBase(event: Event){
        withContext(Dispatchers.IO){
            eventDao.insert(event)
        }
    }



    private suspend fun getExpensesFromDataBase(eventId: Long) : LiveData<List<Expense>>{
        return withContext(Dispatchers.IO){
            expenseDao.getAll(eventId)
        }
    }

    fun addDeleteEvent(eventId: Long){
        _deleteEventId.value = eventId
    }

    fun resetDeleteEvent(){
        _deleteEventId.value = null
    }

    fun onEventClicked(id: Long){
        _idEvent.value = id
    }

    fun onEventNavigated(){
        _idEvent.value = null
    }

}