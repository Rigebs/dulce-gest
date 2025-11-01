package com.rige.dulcegest.domain.usecases.products.supplies

import com.rige.dulcegest.data.local.entities.Supply
import com.rige.dulcegest.data.repository.SupplyRepository
import jakarta.inject.Inject
import org.threeten.bp.LocalDateTime

class SaveSupplyUseCase @Inject constructor(
    private val repo: SupplyRepository
) {
    suspend fun execute(
        id: Long,
        name: String,
        unit: String,
        stock: Double,
        purchaseUnit: String?,
        conversionFactor: Double?,
        notes: String?
    ): Boolean {
        // 1. Validación de Dominio (Aunque la validación de campos vacíos puede seguir en la UI)
        if (name.isEmpty() || unit.isEmpty()) {
            return false
        }

        // 2. Construcción del objeto de Dominio (Supply)
        val supply = Supply(
            id = id,
            name = name.trim(),
            unit = unit,
            stockQty = stock,
            purchaseUnit = purchaseUnit?.trim(),
            conversionFactor = conversionFactor,
            updatedAt = LocalDateTime.now().toString(),
            notes = notes?.trim()
        )

        // 3. Ejecución de la acción: Insertar o Actualizar
        if (id == 0L) {
            repo.insert(supply)
        } else {
            repo.update(supply)
        }
        return true
    }
}