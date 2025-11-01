package com.rige.dulcegest.domain.usecases.finances.shopping

import com.rige.dulcegest.data.local.entities.ShoppingListItem
import com.rige.dulcegest.data.repository.ShoppingListRepository
import javax.inject.Inject

class ToggleShoppingItemPurchasedStatusUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    /**
     * Alterna el estado 'purchased' de un ítem de la lista de compras y lo actualiza en la base de datos.
     */
    suspend fun execute(item: ShoppingListItem) {
        // Lógica de negocio centralizada: invertir el estado
        val newItem = item.copy(purchased = !item.purchased)
        repository.update(newItem)
    }
}