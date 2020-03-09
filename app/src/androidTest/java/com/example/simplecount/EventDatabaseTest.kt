package com.example.simplecount

import androidx.room.Room
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.simplecount.data.db.EventDatabase
import com.example.simplecount.data.db.dao.EventDao
import com.example.simplecount.data.db.entity.Event
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4ClassRunner::class)
class EventDatabaseTest {

    private lateinit var eventDao: EventDao
    private lateinit var db: EventDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, EventDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        eventDao = db.eventDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetEvent() {
        val event = Event()
        var anotherEvent = Event()
        anotherEvent.title = "hello"
        eventDao.insert(anotherEvent)
        eventDao.insert(event)
        val lastEvent = eventDao.get(1L)
        assertEquals(lastEvent?.title, "hello")
    }
}