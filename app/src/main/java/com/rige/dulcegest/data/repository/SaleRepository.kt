package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.db.dao.SaleDao
import com.rige.dulcegest.data.db.dao.SaleItemDao
import com.rige.dulcegest.data.db.entities.Sale
import com.rige.dulcegest.data.db.entities.SaleItem
import jakarta.inject.Inject

class SaleRepository @Inject constructor(
    private val saleDao: SaleDao,
    private val itemDao: SaleItemDao
) {
    val allSales: LiveData<List<Sale>> = saleDao.getAll()

    suspend fun insertSale(sale: Sale, items: List<SaleItem>) {
        val saleId = saleDao.insert(sale)
        val itemList = items.map { it.copy(saleId = saleId) }
        itemDao.insertAll(itemList)
    }

    suspend fun getSaleWithItems(id: Long) = saleDao.getSaleWithItems(id)

    suspend fun deleteSale(sale: Sale) = saleDao.delete(sale)
}
