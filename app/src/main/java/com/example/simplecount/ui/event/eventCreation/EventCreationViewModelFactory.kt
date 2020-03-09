package com.example.simplecount.ui.event.eventCreation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplecount.data.db.dao.EventDao

class EventCreationViewModelFactory (private val eventDao: EventDao, private val application: Application) : ViewModelProvider.Factory{

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventCreationViewModel::class.java)){
            return EventCreationViewModel(eventDao, application) as T
        }
        throw IllegalArgumentException ("unknown ViewModel class")
    }

}