package com.rige.dulcegest.domain.usecases.finances.sales

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.repository.SaleRepository
import jakarta.inject.Inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GetTotalSalesTodayUseCase @Inject constructor(
    private val repo: SaleRepository
) {
    /**
     * Obtiene el total de ventas para el día actual.
     */
    fun execute(): LiveData<Double?> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        
        // Asume que SaleRepository tiene getTotalSalesBetween(start, end)
        return repo.getTotalSalesBetween(today, today)
    }
}