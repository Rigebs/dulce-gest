package com.rige.dulcegest.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rige.dulcegest.data.local.entities.Expense

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

    @Query("SELECT IFNULL(SUM(amount), 0) FROM expenses")
    fun getTotalExpenses(): LiveData<Double>

    @Query("SELECT IFNULL(SUM(amount), 0) FROM expenses WHERE DATE(date) = DATE('now', 'localtime')")
    fun getTotalExpensesToday(): LiveData<Double>

    @Query("SELECT IFNULL(SUM(amount), 0) FROM expenses WHERE DATE(date) >= DATE('now', '-6 days', 'localtime')")
    fun getTotalExpensesThisWeek(): LiveData<Double>

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
}