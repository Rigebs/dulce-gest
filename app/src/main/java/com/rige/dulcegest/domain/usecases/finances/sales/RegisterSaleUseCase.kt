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
    /**
     * Registra una venta completa (cabecera + ítems) y actualiza el stock de los productos vendidos
     * de forma transaccional.
     */
    suspend fun execute(sale: Sale, items: List<SaleItem>) {
        if (items.isEmpty()) {
            throw IllegalArgumentException("La venta debe contener al menos un artículo.")
        }

        // 1. 🥇 Guardar la Venta y sus ítems (delega al repositorio para la atomicidad/transacción)
        // Se asume que SaleRepository tiene un método transaccional:
        val saleId = saleRepo.insertSaleTransaction(sale, items)

        // 2. 📉 Actualizar el stock de cada producto vendido
        items.forEach { item ->
            val product = productRepo.getProductByIdSuspend(item.productId)

            // Cantidad vendida en unidades base: qty (uds vendidas) * presentationQuantity (factor de conversión a base)
            val soldBaseQty = item.qty * (item.presentationQuantity ?: 1.0)

            product?.let { currentProduct ->
                val newStock = currentProduct.stockQty - soldBaseQty

                // Actualizar stock del producto
                val updatedProduct = currentProduct.copy(stockQty = newStock)
                // Se asume que ProductRepository tiene un método 'update(Product)'
                productRepo.update(updatedProduct)
            }
            // NOTA: La lógica de manejo de insuficiencia de stock (stock < 0) debería ir aquí.
        }
    }
}