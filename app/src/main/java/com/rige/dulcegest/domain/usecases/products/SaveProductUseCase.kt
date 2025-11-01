package com.rige.dulcegest.domain.usecases.products

import com.rige.dulcegest.data.local.entities.Product
import com.rige.dulcegest.data.local.entities.ProductPresentation
import com.rige.dulcegest.data.local.entities.ProductRecipe
import com.rige.dulcegest.data.local.entities.ProductVariant
import com.rige.dulcegest.data.repository.ProductRepository
import jakarta.inject.Inject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class SaveProductUseCase @Inject constructor(
    private val repo: ProductRepository
) {
    /**
     * Guarda o actualiza un producto junto con sus detalles (Receta, Presentaciones y Variantes).
     *
     * @param currentProduct El producto existente (si es edición) o null (si es nuevo).
     * @param name Nombre del producto.
     * @param unit Unidad de medida base.
     * @param price Precio base.
     * @param notes Notas opcionales.
     * @param imagePath Ruta de la imagen.
     * @param recipeItems La lista de ítems de la receta (con IDs temporales si es nuevo).
     * @param presentations La lista de presentaciones.
     * @param variants La lista de variantes.
     * @return El ID del producto guardado.
     */
    suspend fun execute(
        currentProduct: Product?,
        name: String,
        unit: String,
        price: Double,
        notes: String?,
        imagePath: String?,
        recipeItems: List<ProductRecipe>,
        presentations: List<ProductPresentation>,
        variants: List<ProductVariant>
    ): Long {
        if (name.isEmpty()) {
            throw IllegalArgumentException("El nombre del producto no puede estar vacío.")
        }

        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        val product = currentProduct?.copy(
            name = name,
            unit = unit,
            price = price,
            notes = notes,
            imagePath = imagePath,
            updatedAt = now
        ) ?: Product(
            name = name,
            unit = unit,
            price = price,
            stockQty = 0.0,
            createdAt = now,
            notes = notes,
            imagePath = imagePath
        )

        // El Repositorio manejará la lógica transaccional de guardado/actualización
        // y la inserción/sincronización de los detalles (receta, presentaciones, variantes).
        return repo.saveProductTransaction(
            product,
            recipeItems,
            presentations,
            variants
        )
    }
}