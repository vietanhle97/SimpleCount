package com.example.simplecount.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.simplecount.data.db.entity.Expense

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(expense: Expense) : Long

    @Update
    fun update(expense: Expense)

    @Delete
    fun delete(expense: Expense)

    @Query("DELETE FROM expense_table WHERE eventId = :eventId")
    fun deleteAll(eventId: Long)

    @Query("SELECT * FROM expense_table WHERE id = :id_")
    fun get(id_ : Long) : LiveData<Expense>

    @Query("SELECT * FROM expense_table WHERE eventId = :eventId")
    fun getAll(eventId : Long) : LiveData<List<Expense>>

    @Query("DELETE FROM expense_table")
    fun deleteAllDatabase()

    @Query("SELECT * FROM expense_table")
    fun getAllExpenseDB() : LiveData<List<Expense>>

    @Query("SELECT * FROM expense_table ORDER BY id DESC LIMIT 1")
    fun getLast() : Expense?
}