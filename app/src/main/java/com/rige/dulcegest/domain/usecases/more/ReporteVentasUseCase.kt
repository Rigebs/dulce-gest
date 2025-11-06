package com.rige.dulcegest.domain.usecases.more

import com.rige.dulcegest.data.repository.SaleRepository
import javax.inject.Inject

class SalesReportUseCase @Inject constructor(
    private val repository: SaleRepository
) {

    suspend fun getTotalSalesByDateRange(startDate: String, endDate: String): Double {
        return repository.getTotalSalesByDateRange(startDate, endDate) ?: 0.0
    }
}