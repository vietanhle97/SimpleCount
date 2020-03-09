package com.example.simplecount.ui.event.eventCreation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.simplecount.data.db.dao.EventDao
import com.example.simplecount.data.db.entity.Event
import kotlinx.coroutines.*

class EventCreationViewModel (
    val eventDao: EventDao,
    application: Application)
    : AndroidViewModel(application){

    private var eventViewModelJob = Job()
    private val uiScope = CoroutineScope(eventViewModelJob + Dispatchers.Main)

    private val _id = MutableLiveData<Long>()
    val id : LiveData<Long> get() = _id

    override fun onCleared() {
        super.onCleared()
        eventViewModelJob.cancel()
    }

    fun insertEvent(e: Event) {
        uiScope.launch {
            insertEventToDatabase(e)
        }
    }

    private suspend fun insertEventToDatabase(e: Event) {
        return withContext(Dispatchers.IO) {
            _id.postValue(eventDao.insert(e))
        }
    }

    fun resetId(){
        _id.value = null
    }
}