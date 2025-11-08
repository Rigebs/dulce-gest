package com.rige.dulcegest.ui.more.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rige.dulcegest.data.repository.SaleRepository
import com.rige.dulcegest.domain.models.DailySalesResult
import com.rige.dulcegest.domain.models.DailySalesSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SalesReportsViewModel @Inject constructor(
    private val saleRepository: SaleRepository
) : ViewModel() {

    private val dateFormatterDB = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dateFormatterUI = DateTimeFormatter.ofPattern("dd MMM")

    // --- Reporte Diario (Hoy, Ayer, Anteayer, etc.) ---
    private val _dailySalesSummary = MutableLiveData<List<DailySalesSummary>>()
    val dailySalesSummary: LiveData<List<DailySalesSummary>> = _dailySalesSummary

    // --- Reporte Semanal Mensual (Semanas del Mes) ---
    private val _currentYearMonth = MutableLiveData(YearMonth.now())
    val currentYearMonth: LiveData<YearMonth> = _currentYearMonth

    private val _monthlyWeeklySales = MutableLiveData<List<DailySalesSummary>>()
    val monthlyWeeklySales: LiveData<List<DailySalesSummary>> = _monthlyWeeklySales


    init {
        loadDailyReport(7) // Cargar los últimos 7 días
        loadMonthlyReport(_currentYearMonth.value!!)
    }

    /**
     * Carga el resumen de ventas para los últimos [days] días.
     */
    fun loadDailyReport(days: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val today = LocalDate.now()
            val startDate = today.minusDays(days - 1)
            val endDate = today

            val startDateStr = startDate.format(dateFormatterDB)
            val endDateStr = endDate.format(dateFormatterDB)

            val rawData = saleRepository.getDailySalesByDateRange(startDateStr, endDateStr)
            // Mapeo a DailySalesSummary con etiquetas como "Hoy", "Ayer"
            _dailySalesSummary.postValue(mapToDailySummary(rawData, today))
        }
    }

    fun setMonth(yearMonth: YearMonth) {
        _currentYearMonth.value = yearMonth
        loadMonthlyReport(yearMonth)
    }

    private fun loadMonthlyReport(yearMonth: YearMonth) {
        viewModelScope.launch(Dispatchers.IO) {
            val monthlyData = mutableListOf<DailySalesSummary>()

            var currentDate = yearMonth.atDay(1)
            val endOfMonth = yearMonth.atEndOfMonth()

            while (currentDate.isBefore(endOfMonth) || currentDate.isEqual(endOfMonth)) {
                val startOfWeek = currentDate.with(DayOfWeek.MONDAY)
                var endOfWeek = startOfWeek.plusDays(6)

                if (endOfWeek.isAfter(endOfMonth)) {
                    endOfWeek = endOfMonth
                }

                val effectiveStart = if (startOfWeek.isBefore(currentDate)) currentDate else startOfWeek

                val startStr = effectiveStart.format(dateFormatterDB)
                val endStr = endOfWeek.format(dateFormatterDB)

                val total = saleRepository.getTotalSalesBetweenSuspend(startStr, endStr)

                val key = "${effectiveStart.format(dateFormatterUI)} - ${endOfWeek.format(dateFormatterUI)}"

                monthlyData.add(
                    DailySalesSummary(
                        dateOrPeriodLabel = key,
                        totalSales = total,
                        date = startStr
                    )
                )

                currentDate = endOfWeek.plusDays(1)
            }

            _monthlyWeeklySales.postValue(monthlyData)
        }
    }

    private fun mapToDailySummary(rawData: List<DailySalesResult>, today: LocalDate): List<DailySalesSummary> {
        val todayStr = today.format(dateFormatterDB)
        val yesterdayStr = today.minusDays(1).format(dateFormatterDB)
        val dayBeforeYesterdayStr = today.minusDays(2).format(dateFormatterDB)

        return rawData.map { item ->
            val dateParsed = LocalDate.parse(item.sale_date, dateFormatterDB)
            val label = when (item.sale_date) {
                todayStr -> "Hoy"
                yesterdayStr -> "Ayer"
                dayBeforeYesterdayStr -> "Anteayer"
                else -> "${dateParsed.dayOfWeek.getDisplayName(
                    TextStyle.FULL,
                    Locale("es", "ES")
                ).replaceFirstChar { it.uppercase() }} (${dateParsed.format(dateFormatterUI)})"
            }
            DailySalesSummary(
                dateOrPeriodLabel = label,
                totalSales = item.total_sales,
                date = item.sale_date
            )
        }
    }
}