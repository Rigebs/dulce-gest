package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.local.dao.ProductDao
import com.rige.dulcegest.data.local.dao.SaleDao
import com.rige.dulcegest.data.local.dao.SaleItemDao
import com.rige.dulcegest.data.local.entities.Sale
import com.rige.dulcegest.data.local.entities.SaleItem
import com.rige.dulcegest.data.local.entities.relations.SaleWithItems
import com.rige.dulcegest.domain.models.DailySalesResult
import jakarta.inject.Inject

class SaleRepository @Inject constructor(
    private val saleDao: SaleDao,
    private val itemDao: SaleItemDao,
    private val productDao: ProductDao
) {

    val lastFiveSales: LiveData<List<SaleWithItems>> = saleDao.getLastFiveSales()

    val salesOfThisWeek: LiveData<List<SaleWithItems>> = saleDao.getSalesThisWeek()

    suspend fun insertSaleTransaction(sale: Sale, items: List<SaleItem>): Long {
        val saleId = saleDao.insert(sale)

        val itemList = items.map { it.copy(saleId = saleId) }
        itemDao.insertAll(itemList)

        itemList.forEach { item ->
            val presentationFactor = item.presentationQuantity
            val qtyToSubtract = item.qty * presentationFactor
            productDao.reduceStock(item.productId, qtyToSubtract)
        }

        return saleId
    }

    suspend fun getTotalSalesBetweenSuspend(startDate: String, endDate: String): Double {
        return saleDao.getTotalSalesBetweenSuspend(startDate, endDate)
    }

    fun getTotalSalesBetween(startDate: String, endDate: String): LiveData<Double> {
        return saleDao.getTotalSalesBetween(startDate, endDate)
    }

    suspend fun getDailySalesByDateRange(startDate: String, endDate: String): List<DailySalesResult> {
        return saleDao.getDailySalesByDateRange(startDate, endDate)
    }
}