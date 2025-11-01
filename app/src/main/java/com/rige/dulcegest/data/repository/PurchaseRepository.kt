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

    /**
     * Inserta una nueva compra y actualiza el stock y el costo promedio del insumo.
     * Esta operación es la transacción de negocio clave para las compras.
     */
    suspend fun insert(purchase: Purchase): Long {
        // 1. Insertar el registro de la compra
        val purchaseId = dao.insert(purchase)

        // 2. Actualizar stock y costo promedio en la tabla Supply
        supplyDao.updateStockAndCostAfterPurchase(
            id = purchase.supplyId,
            quantity = purchase.quantity,
            totalPrice = purchase.totalPrice
        )
        return purchaseId
    }

    suspend fun getBySupply(supplyId: Long) = dao.getBySupply(supplyId)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    fun getTotalPurchasesThisWeek(): LiveData<Double> = dao.getTotalThisWeek()
}