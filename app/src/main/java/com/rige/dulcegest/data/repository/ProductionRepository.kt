package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.local.dao.ProductionBatchDao
import com.rige.dulcegest.data.local.dao.ProductionConsumptionDao
import com.rige.dulcegest.data.local.dao.ProductDao
import com.rige.dulcegest.data.local.dao.SupplyDao
import com.rige.dulcegest.data.local.entities.ProductionBatch
import com.rige.dulcegest.data.local.entities.ProductionConsumption
import com.rige.dulcegest.data.local.entities.relations.ProductionBatchWithProduct
import com.rige.dulcegest.data.local.entities.relations.ProductionBatchWithProductAndConsumptions
import jakarta.inject.Inject

class ProductionRepository @Inject constructor(
    private val batchDao: ProductionBatchDao,
    private val consumptionDao: ProductionConsumptionDao,
    private val productDao: ProductDao,
    private val supplyDao: SupplyDao
) {
    val allBatches: LiveData<List<ProductionBatchWithProduct>> = batchDao.getBatchesWithProduct()

    val allFullBatches: LiveData<List<ProductionBatchWithProductAndConsumptions>> =
        batchDao.getFullBatches()

    suspend fun getBatchesForProductInPeriod(
        productId: Long,
        startDate: String,
        endDate: String
    ): List<ProductionBatch> {
        return batchDao.getBatchesForProductInPeriod(productId, startDate, endDate)
    }

    suspend fun getBatch(id: Long) = batchDao.getById(id)

    suspend fun getBatchProductConsumptionByIdOnce(id: Long) =
        batchDao.getBatchProductConsumptionByIdOnce(id)

    suspend fun getBatchWithConsumptions(id: Long) = batchDao.getBatchWithConsumptions(id)

    suspend fun getLastFiveBatchesForProduct(productId: Long): List<ProductionBatch> {
        return batchDao.getLastFiveBatchesForProduct(productId)
    }

    suspend fun saveNewBatchTransaction(
        batch: ProductionBatch,
        consumptions: List<ProductionConsumption>
    ) {
        val batchId = batchDao.insert(batch)

        val list = consumptions.map { it.copy(batchId = batchId) }
        consumptionDao.insertAll(list)

        productDao.addStock(batch.productId, batch.quantityProduced)

        list.forEach { consumption ->
            supplyDao.consumeStock(consumption.supplyId, consumption.qtyUsed)
        }
    }

    suspend fun updateBatchTransaction(
        updatedBatch: ProductionBatch,
        oldQty: Double,
        updatedConsumptions: List<ProductionConsumption>
    ) {
        val qtyDelta = updatedBatch.quantityProduced - oldQty

        batchDao.update(updatedBatch)

        if (qtyDelta != 0.0) {
            if (qtyDelta > 0) {
                productDao.addStock(updatedBatch.productId, qtyDelta)
            } else {
                productDao.reduceStock(updatedBatch.productId, -qtyDelta)
            }
        }

        consumptionDao.deleteByBatchId(updatedBatch.id)
        consumptionDao.insertAll(updatedConsumptions)
    }

    suspend fun deleteBatch(batch: ProductionBatch) = batchDao.delete(batch)
}