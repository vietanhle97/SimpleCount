package com.example.simplecount.ui.event.eventDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplecount.data.db.dao.EventDao
import com.example.simplecount.data.db.dao.ExpenseDao
import com.example.simplecount.data.db.entity.Event
import kotlinx.coroutines.*

class EventDetailViewModel (
    val id : Long,
    val eventDao: EventDao,
    val expenseDao: ExpenseDao
) : ViewModel() {

    private var eventViewModelJob = Job()
    private val uiScope = CoroutineScope(eventViewModelJob + Dispatchers.Main)

    private val _currentEvent = MutableLiveData<Event>()
    val currentEvent : LiveData<Event> get() = _currentEvent

    override fun onCleared() {
        super.onCleared()
        eventViewModelJob.cancel()
    }

    init {
        getEvent(id)
    }

    private fun getEvent(id : Long){
        uiScope.launch {
            getEventFromDatabase(id)
        }
    }

    private suspend fun getEventFromDatabase(id: Long){
        return withContext(Dispatchers.IO){
            _currentEvent.postValue(eventDao.get(id))
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

}