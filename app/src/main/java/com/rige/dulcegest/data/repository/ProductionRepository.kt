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

    suspend fun getBatchProductConsumptionByIdOnce(id: Long) =
        batchDao.getBatchProductConsumptionByIdOnce(id)

    suspend fun getBatchWithConsumptions(id: Long) = batchDao.getBatchWithConsumptions(id)

    /**
     * Inserta un nuevo lote de producción y sus consumos, ajustando los stocks de productos e insumos.
     * Esta es la transacción multi-DAO que garantiza la atomicidad.
     */
    suspend fun saveNewBatchTransaction(
        batch: ProductionBatch,
        consumptions: List<ProductionConsumption>
    ) {
        // 1. Insertar el Lote (Cabecera)
        val batchId = batchDao.insert(batch)

        // 2. Insertar los Consumos (Detalle)
        val list = consumptions.map { it.copy(batchId = batchId) }
        consumptionDao.insertAll(list)

        // 3. Actualizar Stock del Producto Producido
        productDao.addStock(batch.productId, batch.quantityProduced)

        // 4. Reducir Stock de Insumos
        list.forEach { consumption ->
            supplyDao.consumeStock(consumption.supplyId, consumption.qtyUsed)
        }
    }

    /**
     * Transacción para actualizar un lote de producción (cantidad) y recalcular su impacto.
     * Esta transacción asume que solo la CANTIDAD y el COSTO total han cambiado proporcionalmente,
     * pero no la lista de insumos.
     *
     * @param updatedBatch El objeto ProductionBatch con la nueva cantidad y costo.
     * @param oldQty La cantidad original producida para calcular la diferencia de stock.
     * @param updatedConsumptions La lista de consumos con las nuevas cantidades usadas y costos.
     */
    suspend fun updateBatchTransaction(
        updatedBatch: ProductionBatch,
        oldQty: Double,
        updatedConsumptions: List<ProductionConsumption>
    ) {
        // 1. Calcular la diferencia de stock del producto terminado.
        val qtyDelta = updatedBatch.quantityProduced - oldQty

        // 2. Actualizar el Lote (Cabecera).
        batchDao.update(updatedBatch)

        // 3. Ajustar Stock del Producto Producido.
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