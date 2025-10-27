package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.local.dao.ProductDao
import com.rige.dulcegest.data.local.dao.SaleDao
import com.rige.dulcegest.data.local.dao.SaleItemDao
import com.rige.dulcegest.data.local.entities.Sale
import com.rige.dulcegest.data.local.entities.relations.SaleWithItems
import jakarta.inject.Inject

class SaleRepository @Inject constructor(
    private val saleDao: SaleDao,
    private val itemDao: SaleItemDao,
    private val productDao: ProductDao
) {
    val allSales: LiveData<List<Sale>> = saleDao.getAll()

    val lastFiveSales: LiveData<List<SaleWithItems>> = saleDao.getLastFiveSales()

    val salesOfThisWeek: LiveData<List<SaleWithItems>> = saleDao.getSalesThisWeek()

    suspend fun insertSale(sale: Sale, items: List<com.rige.dulcegest.data.local.entities.SaleItem>) {
        val saleId = saleDao.insert(sale)
        val itemList = items.map { it.copy(saleId = saleId) }
        itemDao.insertAll(itemList)

        itemList.forEach { item ->
            val qtyToSubtract = item.qty * item.presentationQuantity
            productDao.reduceStock(item.productId, qtyToSubtract)
        }
    }

    suspend fun getSaleWithItems(id: Long) = saleDao.getSaleWithItems(id)

    suspend fun deleteSale(sale: Sale) = saleDao.delete(sale)

    fun getTotalSalesToday() = saleDao.getTotalSalesToday()
    fun getTotalSalesThisWeek() = saleDao.getTotalSalesThisWeek()

    suspend fun deleteAll() {
        saleDao.deleteAllSales()
    }
}
