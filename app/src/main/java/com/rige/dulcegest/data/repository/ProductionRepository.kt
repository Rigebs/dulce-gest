package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.local.dao.ProductionBatchDao
import com.rige.dulcegest.data.local.dao.ProductionConsumptionDao
import com.rige.dulcegest.data.local.entities.ProductionBatch
import com.rige.dulcegest.data.local.entities.ProductionConsumption
import com.rige.dulcegest.data.local.entities.relations.ProductionBatchWithProduct
import com.rige.dulcegest.data.local.entities.relations.ProductionBatchWithProductAndConsumptions
import jakarta.inject.Inject

class ProductionRepository @Inject constructor(
    private val batchDao: ProductionBatchDao,
    private val consumptionDao: ProductionConsumptionDao
) {
    val allBatches: LiveData<List<ProductionBatchWithProduct>> = batchDao.getBatchesWithProduct()

    val allFullBatches: LiveData<List<ProductionBatchWithProductAndConsumptions>> = batchDao.getFullBatches()

    suspend fun getAverageProductionCost(productId: Long): Double {
        return batchDao.getAverageProductionCost(productId) ?: 0.0
    }

    suspend fun getBatchesForProductInPeriod(
        productId: Long,
        startDate: String,
        endDate: String
    ): List<ProductionBatch> {
        return batchDao.getBatchesForProductInPeriod(productId, startDate, endDate)
    }

    suspend fun getBatch(id: Long) = batchDao.getById(id)

    suspend fun getBatchProductConsumptionByIdOnce(id: Long) = batchDao.getBatchProductConsumptionByIdOnce(id)

    suspend fun getBatchWithConsumptions(id: Long) = batchDao.getBatchWithConsumptions(id)

    suspend fun insertBatch(batch: ProductionBatch, consumptions: List<ProductionConsumption>) {
        val batchId = batchDao.insert(batch)
        val list = consumptions.map { it.copy(batchId = batchId) }
        consumptionDao.insertAll(list)
    }

    suspend fun updateBatch(batch: ProductionBatch) = batchDao.update(batch)

    suspend fun deleteBatch(batch: ProductionBatch) = batchDao.delete(batch)

    suspend fun deleteAll() {
        batchDao.deleteAllProductions()
    }
}