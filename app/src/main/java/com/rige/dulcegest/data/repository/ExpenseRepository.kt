package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.db.dao.ExpenseDao
import com.rige.dulcegest.data.db.entities.Expense
import jakarta.inject.Inject

class ExpenseRepository @Inject constructor(
    private val dao: ExpenseDao
) {
    val allExpenses: LiveData<List<Expense>> = dao.getAll()

    suspend fun insert(expense: Expense): Long = dao.insert(expense)
    suspend fun delete(expense: Expense) = dao.delete(expense)
    suspend fun getByCategory(category: String) = dao.getByCategory(category)

    fun getTotalExpenses() = dao.getTotalExpenses()
    fun getTotalExpensesToday() = dao.getTotalExpensesToday()
    fun getTotalExpensesThisWeek() = dao.getTotalExpensesThisWeek()
}