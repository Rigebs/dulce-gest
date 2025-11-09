package com.rige.dulcegest.domain.usecases.finances.sales

import com.rige.dulcegest.data.local.dao.SaleDao
import com.rige.dulcegest.data.local.entities.Sale
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetSalesByDateRangeUseCase @Inject constructor(
    private val saleDao: SaleDao
) {
    fun execute(startDate: String, endDate: String): Flow<List<Sale>> {
        println(startDate)
        println(endDate)
        return saleDao.getSalesByDateRange(startDate, endDate)
    }
}