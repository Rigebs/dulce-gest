package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.local.dao.PurchaseDao
import com.rige.dulcegest.data.local.dao.SupplyDao
import com.rige.dulcegest.data.local.entities.Purchase
import jakarta.inject.Inject

class PurchaseRepository @Inject constructor(
    private val dao: PurchaseDao,
    private val supplyDao: SupplyDao
) {
    val allPurchases: LiveData<List<Purchase>> = dao.getAll()

    suspend fun getById(id: Long): Purchase? = dao.getById(id)

    suspend fun insert(purchase: Purchase): Long {

        val purchaseId = dao.insert(purchase)

        val currentSupply = supplyDao.getByIdOnce(purchase.supplyId)
            ?: throw IllegalStateException("Supply con ID ${purchase.supplyId} no encontrada")

        val factor = currentSupply.conversionFactor ?: 1.0
        val addedQty = purchase.quantity * factor

        val newUnitCost = purchase.totalPrice / addedQty

        val oldStock = currentSupply.stockQty
        val oldCost = currentSupply.avgCost
        val totalStockAfterPurchase = oldStock + addedQty

        val newAvgCost = if (totalStockAfterPurchase > 0) {
            ((oldStock * oldCost) + (addedQty * newUnitCost)) / totalStockAfterPurchase
        } else {
            newUnitCost
        }

        val newStock = totalStockAfterPurchase

        supplyDao.updateStockAndCost(
            id = currentSupply.id,
            newStock = newStock,
            newAvgCost = newAvgCost
        )
        return purchaseId
    }

    suspend fun getBySupply(supplyId: Long) = dao.getBySupply(supplyId)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    fun getTotalPurchasesThisWeek(): LiveData<Double> = dao.getTotalThisWeek()
}