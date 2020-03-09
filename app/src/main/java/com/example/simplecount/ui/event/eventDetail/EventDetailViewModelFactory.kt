package com.example.simplecount.ui.event.eventDetail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplecount.data.db.dao.EventDao
import com.example.simplecount.data.db.dao.ExpenseDao
import com.example.simplecount.ui.event.eventCreation.EventCreationViewModel

class EventDetailViewModelFactory (private val id: Long, private val eventDao: EventDao, private val expenseDao: ExpenseDao) : ViewModelProvider.Factory{

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventDetailViewModel::class.java)){
            return EventDetailViewModel(id, eventDao, expenseDao) as T
        }
        throw IllegalArgumentException ("unknown ViewModel class")
    }

}