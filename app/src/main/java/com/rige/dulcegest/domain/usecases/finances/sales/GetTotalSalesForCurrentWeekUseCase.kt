package com.rige.dulcegest.domain.usecases.finances.sales

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.repository.SaleRepository
import jakarta.inject.Inject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GetTotalSalesForCurrentWeekUseCase @Inject constructor(
    private val repo: SaleRepository
) {
    fun execute(): LiveData<Double> {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val start = calendar.time

        val end = Calendar.getInstance().time

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return repo.getTotalSalesBetween(dateFormat.format(start), dateFormat.format(end))
    }
}