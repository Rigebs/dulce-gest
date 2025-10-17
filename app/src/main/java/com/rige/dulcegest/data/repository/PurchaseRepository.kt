package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.db.dao.PurchaseDao
import com.rige.dulcegest.data.db.entities.Purchase
import jakarta.inject.Inject

class PurchaseRepository @Inject constructor(
    private val dao: PurchaseDao
) {
    val allPurchases: LiveData<List<Purchase>> = dao.getAll()

    suspend fun getById(id: Long): Purchase? = dao.getById(id)

    suspend fun insert(purchase: Purchase): Long = dao.insert(purchase)

    suspend fun getByIngredient(ingredientId: Long) = dao.getByIngredient(ingredientId)

    suspend fun deleteById(id: Long) = dao.deleteById(id)
}