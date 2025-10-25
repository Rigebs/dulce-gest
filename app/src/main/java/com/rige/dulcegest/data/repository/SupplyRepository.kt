package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.local.dao.SupplyDao
import com.rige.dulcegest.data.local.entities.Supply
import jakarta.inject.Inject

class SupplyRepository @Inject constructor(
    private val dao: SupplyDao
) {
    val allSupplies: LiveData<List<Supply>> = dao.getAll()

    fun getById(id: Long): LiveData<Supply?> = dao.getById(id)

    suspend fun insert(supply: Supply): Long = dao.insert(supply)

    suspend fun update(supply: Supply) = dao.update(supply)

    suspend fun delete(supply: Supply) = dao.delete(supply)

    suspend fun addStock(id: Long, qty: Double) = dao.addStock(id, qty)

    suspend fun consumeStock(id: Long, qty: Double) = dao.consumeStock(id, qty)

    fun getLowStock(threshold: Double = 5.0): LiveData<List<Supply>> =
        dao.getLowStock(threshold)

    suspend fun getAllOnce(): List<Supply> = dao.getAllOnce()
}