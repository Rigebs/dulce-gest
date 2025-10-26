package com.rige.dulcegest.ui.finances.expenses

import androidx.lifecycle.*
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

    fun getByCategory(category: String) = liveData {
        emit(repo.getByCategory(category))
    }

    fun getTotalExpenses() = liveData {
        emit(repo.getTotalExpenses())
    }

    fun getTotalExpensesToday() = repo.getTotalExpensesToday()
    fun getTotalExpensesThisWeek() = repo.getTotalExpensesThisWeek()

    fun deleteAll() = viewModelScope.launch {
        repo.deleteAll()
    }
}