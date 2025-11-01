package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.local.dao.ExpenseDao
import com.rige.dulcegest.data.local.entities.Expense
import jakarta.inject.Inject

class ExpenseRepository @Inject constructor(
    private val dao: ExpenseDao
) {
    val allExpenses: LiveData<List<Expense>> = dao.getAll()

    suspend fun insert(expense: Expense): Long = dao.insert(expense)
    suspend fun delete(expense: Expense) = dao.delete(expense)

    fun getTotalExpensesThisWeek() = dao.getTotalExpensesThisWeek()

    suspend fun getTotalExpensesThisWeekSuspend(): Double? {
        return dao.getTotalExpensesThisWeekSuspend()
    }
}