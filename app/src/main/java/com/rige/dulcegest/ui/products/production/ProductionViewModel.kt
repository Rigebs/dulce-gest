package com.rige.dulcegest.ui.products.production

import androidx.lifecycle.*
import com.rige.dulcegest.data.local.entities.ProductionBatch
import com.rige.dulcegest.data.local.entities.ProductionConsumption
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

    fun saveBatch(batch: ProductionBatch): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            try {
                repo.insertBatch(batch, emptyList())
                result.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
                result.postValue(false)
            }
        }
        return result
    }
}