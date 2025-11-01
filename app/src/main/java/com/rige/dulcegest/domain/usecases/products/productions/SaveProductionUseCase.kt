package com.rige.dulcegest.domain.usecases.products.productions

import com.rige.dulcegest.data.local.entities.Product
import com.rige.dulcegest.data.local.entities.ProductionBatch
import com.rige.dulcegest.data.local.entities.ProductionConsumption
import com.rige.dulcegest.data.repository.ProductionRepository
import com.rige.dulcegest.data.repository.SupplyRepository
import jakarta.inject.Inject
import org.threeten.bp.LocalDateTime
import kotlin.collections.iterator

class SaveProductionUseCase @Inject constructor(
    private val productionRepo: ProductionRepository,
    private val supplyRepo: SupplyRepository // Solo se necesita para obtener costos
) {

    // Sellada para manejar los posibles resultados y mensajes de error
    sealed class Result {
        data class Success(val totalCost: Double) : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun execute(
        selectedProduct: Product,
        qtyProduced: Double,
        supplyUsages: Map<Long, Double>,
        notes: String?
    ): Result {
        // 1. Validar Cantidad Producida
        if (qtyProduced <= 0) {
            return Result.Error("Ingrese una cantidad producida válida (mayor a 0).")
        }

        // 2. Validar Insumos Usados
        val validUsages = supplyUsages.filter { it.value > 0 }
        if (validUsages.isEmpty()) {
            return Result.Error("Debe ingresar al menos un insumo utilizado.")
        }

        // 3. Calcular Costos y Consumiciones
        val allSupplies = supplyRepo.getAllOnce()
        var totalCost = 0.0
        val consumptions = mutableListOf<ProductionConsumption>()

        for ((supplyId, usedQty) in validUsages) {
            val supply = allSupplies.find { it.id == supplyId }

            // Validación de existencia y uso de Costo Promedio
            if (supply == null) continue

            val cost = usedQty * supply.avgCost // Costo de insumo = cantidad usada * Costo Promedio
            totalCost += cost

            consumptions.add(
                ProductionConsumption(
                    batchId = 0L, // Placeholder
                    supplyId = supplyId,
                    qtyUsed = usedQty,
                    cost = cost
                )
            )
        }

        // 4. Crear el Lote de Producción
        val batch = ProductionBatch(
            productId = selectedProduct.id,
            quantityProduced = qtyProduced,
            totalCost = totalCost,
            date = LocalDateTime.now().toString(),
            notes = notes
        )

        // 5. Persistir Transacción (Delegada al Repositorio)
        // El Repositorio maneja: Inserción de lote/consumos, adición de stock de producto, reducción de stock de insumos.
        productionRepo.saveNewBatchTransaction(batch, consumptions)

        return Result.Success(totalCost)
    }
}