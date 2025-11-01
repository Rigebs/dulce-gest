package com.rige.dulcegest.domain.usecases.finances.purchases

import com.rige.dulcegest.data.local.entities.Purchase
import com.rige.dulcegest.data.local.entities.Supply
import com.rige.dulcegest.data.repository.PurchaseRepository
import com.rige.dulcegest.data.repository.ShoppingListRepository
import com.rige.dulcegest.data.repository.SupplyRepository
import jakarta.inject.Inject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class RegisterSupplyPurchaseUseCase @Inject constructor(
    private val purchaseRepo: PurchaseRepository,
    private val supplyRepo: SupplyRepository,
    private val shoppingListRepo: ShoppingListRepository
) {
    /**
     * Registra una compra, actualiza el stock y costo promedio del insumo,
     * y elimina el insumo de la lista de compras.
     */
    suspend fun execute(
        selectedSupply: Supply,
        purchaseQuantity: Double,
        totalPrice: Double,
        supplier: String?,
        notes: String?
    ) {
        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        // 1. 🥇 Registrar la Compra
        val purchase = Purchase(
            supplyId = selectedSupply.id,
            quantity = purchaseQuantity, // Cantidad en unidades de compra
            totalPrice = totalPrice,
            supplier = supplier,
            date = now,
            notes = notes
        )
        purchaseRepo.insert(purchase)

        // 2. 🗑️ Eliminar ítem de la Lista de Compras
        shoppingListRepo.deleteItemBySupplyId(selectedSupply.id)

        // 3. 📈 Lógica de Actualización de Stock y Costo Promedio (Centralizada aquí)

        // Convertir cantidad de compra a unidades base
        val factor = selectedSupply.conversionFactor ?: 1.0
        val addedQty = purchaseQuantity * factor // cantidad en unidades base

        // Calcular costo por unidad base (precio de compra)
        val newUnitCost = totalPrice / addedQty

        // Recalcular costo promedio ponderado
        val oldStock = selectedSupply.stockQty
        val oldCost = selectedSupply.avgCost

        val newAvgCost = if (oldStock + addedQty > 0) {
            ((oldStock * oldCost) + (addedQty * newUnitCost)) / (oldStock + addedQty)
        } else {
            newUnitCost
        }

        val newStock = oldStock + addedQty

        // 4. 💾 Persistir la actualización del Insumo
        val updatedSupply = selectedSupply.copy(
            stockQty = newStock,
            avgCost = newAvgCost,
            updatedAt = now
        )

        supplyRepo.update(updatedSupply)
    }
}