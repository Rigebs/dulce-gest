package com.rige.dulcegest.domain.usecases.finances.sales

import com.rige.dulcegest.data.local.entities.Sale
import com.rige.dulcegest.data.local.entities.SaleItem
import com.rige.dulcegest.data.repository.ProductRepository
import com.rige.dulcegest.data.repository.SaleRepository
import jakarta.inject.Inject

class RegisterSaleUseCase @Inject constructor(
    private val saleRepo: SaleRepository,
    private val productRepo: ProductRepository
) {
    suspend fun execute(sale: Sale, items: List<SaleItem>) {
        if (items.isEmpty()) {
            throw IllegalArgumentException("La venta debe contener al menos un artículo.")
        }

        saleRepo.insertSaleTransaction(sale, items)

        items.forEach { item ->
            val product = productRepo.getProductByIdSuspend(item.productId)

            val soldBaseQty = item.qty * (item.presentationQuantity ?: 1.0)

            product?.let { currentProduct ->
                val newStock = currentProduct.stockQty - soldBaseQty

                val updatedProduct = currentProduct.copy(stockQty = newStock)
                productRepo.update(updatedProduct)
            }
        }
    }
}