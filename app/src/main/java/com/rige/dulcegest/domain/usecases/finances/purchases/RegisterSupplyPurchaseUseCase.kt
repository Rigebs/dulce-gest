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

    suspend fun execute(
        selectedSupply: Supply,
        purchaseQuantity: Double,
        totalPrice: Double,
        supplier: String?,
        notes: String?
    ) {
        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        val purchase = Purchase(
            supplyId = selectedSupply.id,
            quantity = purchaseQuantity,
            totalPrice = totalPrice,
            supplier = supplier,
            date = now,
            notes = notes
        )
        purchaseRepo.insert(purchase)

        shoppingListRepo.deleteItemBySupplyId(selectedSupply.id)

        val factor = selectedSupply.conversionFactor ?: 1.0
        val addedQty = purchaseQuantity * factor

        val newUnitCost = totalPrice / addedQty

        val oldStock = selectedSupply.stockQty
        val oldCost = selectedSupply.avgCost

        val newAvgCost = if (oldStock + addedQty > 0) {
            ((oldStock * oldCost) + (addedQty * newUnitCost)) / (oldStock + addedQty)
        } else {
            newUnitCost
        }

        val newStock = oldStock + addedQty

        val updatedSupply = selectedSupply.copy(
            stockQty = newStock,
            avgCost = newAvgCost,
            updatedAt = now
        )

        supplyRepo.update(updatedSupply)
    }
}