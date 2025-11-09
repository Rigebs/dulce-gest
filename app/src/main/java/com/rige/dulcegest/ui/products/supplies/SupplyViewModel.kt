package com.rige.dulcegest.ui.products.supplies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _allSupplies: LiveData<List<Supply>> = repo.allSupplies

    private val _filteredSupplies = MutableLiveData<List<Supply>>()
    val filteredSupplies: LiveData<List<Supply>> = _filteredSupplies

    val supplies = repo.allSupplies

    init {
        _allSupplies.observeForever { newList ->
            _filteredSupplies.value = newList
        }
    }

    fun filterSupplies(query: String?) { // <-- Añadido
        val currentList = _allSupplies.value ?: return

        val results = if (query.isNullOrBlank()) {
            currentList
        } else {
            currentList.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        _filteredSupplies.value = results
    }

    fun getSupplyById(id: Long): LiveData<Supply?> = repo.getById(id)

    fun saveSupply(
        id: Long?,
        name: String,
        unit: String,
        stock: Double,
        purchaseUnit: String?,
        conversionFactor: Double?,
        notes: String?
    ) = viewModelScope.launch {
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