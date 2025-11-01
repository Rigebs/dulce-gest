package com.rige.dulcegest.domain.usecases.products.productions

import com.rige.dulcegest.data.repository.ProductionRepository
import jakarta.inject.Inject

class UpdateProductionBatchUseCase @Inject constructor(
    private val productionRepo: ProductionRepository
) {
    suspend fun execute(
        batchId: Long,
        newQty: Double
    ) {
        // 1. Obtener el lote existente con sus consumos
        val batchWithConsumptions = productionRepo.getBatchWithConsumptions(batchId) ?: return

        val oldQty = batchWithConsumptions.batch.quantityProduced

        // Si la cantidad no cambió o la vieja es cero, no hacemos nada para evitar división por cero
        if (oldQty <= 0 || newQty == oldQty) return

        val scaleFactor = newQty / oldQty

        // 2. Recalcular Consumiciones y Costos Total
        var totalCost = 0.0
        val updatedConsumptions = batchWithConsumptions.consumptions.map { c ->
            // Usamos el costo del consumo original para asegurar que el cálculo escale bien,
            // aunque en la práctica, el costo por unidad ya debería ser proporcional.
            val costPerUnit = c.cost / c.qtyUsed

            val newUsedQty = c.qtyUsed * scaleFactor
            val newCost = costPerUnit * newUsedQty // O simplemente c.cost * scaleFactor
            totalCost += newCost

            // Creamos un nuevo objeto de consumo actualizado
            c.copy(qtyUsed = newUsedQty, cost = newCost)
        }

        // 3. Crear el Lote de Producción actualizado
        val updatedBatch = batchWithConsumptions.batch.copy(
            quantityProduced = newQty,
            totalCost = totalCost
        )

        // 4. Persistir la actualización y los consumos a través de la transacción del Repositorio
        productionRepo.updateBatchTransaction(
            updatedBatch,
            oldQty,
            updatedConsumptions
        )
    }
}