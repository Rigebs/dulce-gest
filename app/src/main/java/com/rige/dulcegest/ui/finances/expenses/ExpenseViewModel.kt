package com.rige.dulcegest.ui.finances.expenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rige.dulcegest.data.local.entities.Expense
import com.rige.dulcegest.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repo: ExpenseRepository
) : ViewModel() {

    val expenses = repo.allExpenses

    fun insert(expense: Expense) = viewModelScope.launch {
        repo.insert(expense)
    }

    fun delete(expense: Expense) = viewModelScope.launch {
        repo.delete(expense)
    }

    fun getTotalExpensesThisWeek() = repo.getTotalExpensesThisWeek()
}