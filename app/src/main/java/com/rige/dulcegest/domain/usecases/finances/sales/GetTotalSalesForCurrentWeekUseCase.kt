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
        calendar.firstDayOfWeek = Calendar.MONDAY

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startDate = calendar.time

        val endCalendar = Calendar.getInstance()
        endCalendar.time = startDate
        endCalendar.add(Calendar.DAY_OF_YEAR, 6)

        endCalendar.set(Calendar.HOUR_OF_DAY, 23)
        endCalendar.set(Calendar.MINUTE, 59)
        endCalendar.set(Calendar.SECOND, 59)
        endCalendar.set(Calendar.MILLISECOND, 999)

        val endDate = endCalendar.time

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return repo.getTotalSalesBetween(dateFormat.format(startDate), dateFormat.format(endDate))
    }
}