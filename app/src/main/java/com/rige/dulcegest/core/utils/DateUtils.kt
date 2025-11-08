package com.rige.dulcegest.core.utils

import com.rige.dulcegest.domain.enums.DateRangeFilter
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.TemporalAdjusters
import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {

    fun getRangeStrings(filter: DateRangeFilter): Pair<String, String> {
        val today = LocalDate.now(APP_ZONE_ID)

        val startDate: LocalDate
        val endDate: LocalDate

        when (filter) {
            DateRangeFilter.TODAY -> {
                startDate = today
                endDate = today
            }

            DateRangeFilter.CURRENT_WEEK -> {
                startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                endDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            }

            DateRangeFilter.CURRENT_MONTH -> {
                startDate = today.withDayOfMonth(1)
                endDate = today.with(TemporalAdjusters.lastDayOfMonth())
            }

            DateRangeFilter.ALL_TIME -> {
                return Pair("1970-01-01", "2099-12-31")
            }

            DateRangeFilter.CUSTOM -> {
                return Pair(today.format(ISO_DATE_FORMATTER), today.format(ISO_DATE_FORMATTER))
            }
        }

        return Pair(startDate.format(ISO_DATE_FORMATTER), endDate.format(ISO_DATE_FORMATTER))
    }

    fun getPeriodTitle(filter: DateRangeFilter, start: String?, end: String?): String {
        return when (filter) {
            DateRangeFilter.TODAY -> "Ventas: Hoy"
            DateRangeFilter.CURRENT_WEEK -> "Ventas: Esta Semana"
            DateRangeFilter.CURRENT_MONTH -> "Ventas: Este Mes"
            DateRangeFilter.CUSTOM -> {
                if (start != null && end != null) {
                    val dateFormat = SimpleDateFormat("dd MMM", Locale("es", "ES"))

                    val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(start)
                    val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(end)

                    if (startDate != null && endDate != null) {
                        return "Ventas: ${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}"
                    }
                }
                "Ventas: Rango Personalizado"
            }
            else -> "Ventas: Período Desconocido"
        }
    }
}