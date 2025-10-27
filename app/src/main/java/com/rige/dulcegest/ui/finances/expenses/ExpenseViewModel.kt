package com.rige.dulcegest.ui.finances.expenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rige.dulcegest.data.local.entities.Expense
import com.rige.dulcegest.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    fun deleteAll() = viewModelScope.launch {
        repo.deleteAll()
    }

    fun getTotalExpensesThisWeekOnce(): LiveData<Double?> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val start = calendar.time
        val end = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return repo.getTotalExpensesBetween(dateFormat.format(start), dateFormat.format(end))
    }
}