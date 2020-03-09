package com.example.simplecount.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.simplecount.data.db.entity.Event

@Dao
interface EventDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event : Event) : Long

    @Update
    fun update(event: Event)

    @Delete
    fun delete(event: Event)

    @Query("DELETE FROM event_table")
    fun deleteAll()

    @Query("SELECT * FROM event_table WHERE id = :id_")
    fun get(id_ : Long) : Event?

    @Query("SELECT * FROM event_table ORDER BY id DESC LIMIT 1")
    fun getLast(): Event?

    @Query("SELECT * FROM event_table WHERE id = :id_")
    fun getEvent(id_ : Long) : LiveData<Event>

    @Query("SELECT * FROM event_table")
    fun getAll() : LiveData<List<Event>>

}