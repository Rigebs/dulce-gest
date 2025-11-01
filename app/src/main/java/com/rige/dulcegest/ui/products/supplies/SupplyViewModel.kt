package com.rige.dulcegest.ui.products.supplies

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rige.dulcegest.data.local.entities.Supply
import com.rige.dulcegest.data.repository.SupplyRepository
import com.rige.dulcegest.domain.usecases.products.supplies.SaveSupplyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SupplyViewModel @Inject constructor(
    private val repo: SupplyRepository,
    private val saveSupplyUseCase: SaveSupplyUseCase
) : ViewModel() {

    val supplies = repo.allSupplies

    fun getSupplyById(id: Long): LiveData<Supply?> = repo.getById(id)

    /**
     * 🟢 Centraliza la lógica de insertar/actualizar en el Use Case.
     * La UI solo le pasa los datos crudos.
     */
    fun saveSupply(
        id: Long?,
        name: String,
        unit: String,
        stock: Double,
        purchaseUnit: String?,
        conversionFactor: Double?,
        notes: String?
    ) = viewModelScope.launch {
        // La validación, construcción del objeto Supply y la elección de insert/update
        // se manejan internamente en el Use Case.
        saveSupplyUseCase.execute(
            id ?: 0L,
            name,
            unit,
            stock,
            purchaseUnit,
            conversionFactor,
            notes
        )
    }

    fun delete(supply: Supply) = viewModelScope.launch { repo.delete(supply) }
}