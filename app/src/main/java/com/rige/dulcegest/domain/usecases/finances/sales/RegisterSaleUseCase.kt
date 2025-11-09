package com.rige.dulcegest.domain.usecases.finances.sales

import com.rige.dulcegest.data.local.entities.Sale
import com.rige.dulcegest.data.local.entities.SaleItem
import com.rige.dulcegest.data.repository.SaleRepository
import jakarta.inject.Inject

class RegisterSaleUseCase @Inject constructor(
    private val saleRepo: SaleRepository
) {
    suspend fun execute(sale: Sale, items: List<SaleItem>) {
        if (items.isEmpty()) {
            throw IllegalArgumentException("La venta debe contener al menos un artículo.")
        }

        saleRepo.insertSaleTransaction(sale, items)
    }
}