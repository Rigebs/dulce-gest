package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.local.dao.PurchaseDao
import com.rige.dulcegest.data.local.entities.Purchase
import jakarta.inject.Inject

class PurchaseRepository @Inject constructor(
    private val dao: PurchaseDao
) {
    val allPurchases: LiveData<List<Purchase>> = dao.getAll()

    suspend fun getById(id: Long): Purchase? = dao.getById(id)

    suspend fun insert(purchase: Purchase): Long = dao.insert(purchase)

    suspend fun getBySupply(supplyId: Long) = dao.getBySupply(supplyId)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    fun getTotalPurchasesThisWeek(): LiveData<Double> = dao.getTotalThisWeek()
}