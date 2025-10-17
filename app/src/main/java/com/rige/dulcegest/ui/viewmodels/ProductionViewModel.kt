package com.rige.dulcegest.ui.viewmodels

import androidx.lifecycle.*
import com.rige.dulcegest.data.db.entities.ProductionBatch
import com.rige.dulcegest.data.db.entities.ProductionConsumption
import com.rige.dulcegest.data.repository.ProductionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ProductionViewModel @Inject constructor(
    private val repo: ProductionRepository
) : ViewModel() {

    val batches = repo.allBatches

    fun insertBatch(batch: ProductionBatch, consumptions: List<ProductionConsumption>) =
        viewModelScope.launch {
            repo.insertBatch(batch, consumptions)
        }

    fun getBatchWithConsumptions(id: Long) = liveData {
        emit(repo.getBatchWithConsumptions(id))
    }

    fun deleteBatch(batch: ProductionBatch) = viewModelScope.launch {
        repo.deleteBatch(batch)
    }
}