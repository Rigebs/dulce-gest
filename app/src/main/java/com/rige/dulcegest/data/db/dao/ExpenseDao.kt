package com.rige.dulcegest.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rige.dulcegest.data.db.entities.Expense

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAll(): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    suspend fun getByCategory(category: String): List<Expense>

    @Insert
    suspend fun insert(expense: Expense): Long

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT SUM(amount) FROM expenses")
    suspend fun getTotalExpenses(): Double
}
