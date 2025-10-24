package com.rige.dulcegest.ui.viewmodels

import androidx.lifecycle.*
import com.rige.dulcegest.data.db.entities.Supply
import com.rige.dulcegest.data.repository.SupplyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SupplyViewModel @Inject constructor(
    private val repo: SupplyRepository
) : ViewModel() {

    val supplies = repo.allSupplies

    fun getSupplyById(id: Long): LiveData<Supply?> = repo.getById(id)

    fun insert(supply: Supply) = viewModelScope.launch { repo.insert(supply) }
    fun update(supply: Supply) = viewModelScope.launch { repo.update(supply) }
    fun delete(supply: Supply) = viewModelScope.launch { repo.delete(supply) }
    fun addStock(id: Long, qty: Double) = viewModelScope.launch { repo.addStock(id, qty) }
    fun consumeStock(id: Long, qty: Double) = viewModelScope.launch { repo.consumeStock(id, qty) }

    suspend fun getAllSuppliesOnce(): List<Supply> {
        return repo.getAllOnce()
    }
}