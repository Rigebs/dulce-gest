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
import kotlinx.coroutines.flow.Flow

class SaleRepository @Inject constructor(
    private val saleDao: SaleDao,
    private val itemDao: SaleItemDao,
    private val productDao: ProductDao
) {
    val allSales: LiveData<List<Sale>> = saleDao.getAll()

    val lastFiveSales: LiveData<List<SaleWithItems>> = saleDao.getLastFiveSales()

    val salesOfThisWeek: LiveData<List<SaleWithItems>> = saleDao.getSalesThisWeek()

    suspend fun insertSaleTransaction(sale: Sale, items: List<SaleItem>): Long {
        val saleId = saleDao.insert(sale)

        val itemList = items.map { it.copy(saleId = saleId) }
        itemDao.insertAll(itemList)

        itemList.forEach { item ->
            val presentationFactor = item.presentationQuantity ?: 1.0
            val qtyToSubtract = item.qty * presentationFactor
            productDao.reduceStock(item.productId, qtyToSubtract)
        }

        return saleId
    }

    fun getTotalSalesBetween(startDate: String, endDate: String): LiveData<Double?> {
        return saleDao.getTotalSalesBetween(startDate, endDate)
    }

    suspend fun getSaleWithItems(id: Long) = saleDao.getSaleWithItems(id)

    suspend fun deleteSale(sale: Sale) = saleDao.delete(sale)

    fun getTotalSalesByDateRange(startDate: String, endDate: String): Double? {
        return saleDao.getTotalSalesByDateRange(startDate, endDate)
    }

    fun getSalesByDateRange(startDate: String, endDate: String): Flow<List<Sale>> {
        return saleDao.getSalesByDateRange(startDate, endDate)
    }

    suspend fun getDailySalesByDateRange(startDate: String, endDate: String): List<DailySalesResult> {
        return saleDao.getDailySalesByDateRange(startDate, endDate)
    }
}