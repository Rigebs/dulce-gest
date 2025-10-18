package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.db.dao.ProductionBatchDao
import com.rige.dulcegest.data.db.dao.ProductionConsumptionDao
import com.rige.dulcegest.data.db.entities.ProductionBatch
import com.rige.dulcegest.data.db.entities.ProductionConsumption
import com.rige.dulcegest.data.db.relations.ProductionBatchWithProduct
import jakarta.inject.Inject

class ProductionRepository @Inject constructor(
    private val batchDao: ProductionBatchDao,
    private val consumptionDao: ProductionConsumptionDao
) {
    val allBatches: LiveData<List<ProductionBatchWithProduct>> = batchDao.getBatchesWithProduct()

    suspend fun getBatch(id: Long) = batchDao.getById(id)

    suspend fun getBatchWithConsumptions(id: Long) = batchDao.getBatchWithConsumptions(id)

    suspend fun insertBatch(batch: ProductionBatch, consumptions: List<ProductionConsumption>) {
        val batchId = batchDao.insert(batch)
        val list = consumptions.map { it.copy(batchId = batchId) }
        consumptionDao.insertAll(list)
    }

    suspend fun deleteBatch(batch: ProductionBatch) = batchDao.delete(batch)
}