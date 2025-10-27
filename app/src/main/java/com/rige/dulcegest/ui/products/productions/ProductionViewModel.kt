package com.rige.dulcegest.ui.products.productions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
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

    val batchesWithProductAndConsumptions = repo.allFullBatches

    suspend fun getBatchesForProductInPeriod(productId: Long, startDate: String, endDate: String): List<ProductionBatch> {
        return repo.getBatchesForProductInPeriod(productId, startDate, endDate)
    }

    suspend fun getBatchesForProductInPeriodSuspend(productId: Long, startDate: String, endDate: String): List<ProductionBatch> {
        return repo.getBatchesForProductInPeriod(productId, startDate, endDate)
    }

    fun insertBatch(batch: ProductionBatch, consumptions: List<ProductionConsumption>) =
        viewModelScope.launch {
            repo.insertBatch(batch, consumptions)
        }

    fun getBatchWithConsumptions(id: Long) = liveData {
        emit(repo.getBatchWithConsumptions(id))
    }

    fun getBatchById(id: Long) = liveData {
        emit(repo.getBatch(id))
    }

    fun getBatchProductConsumptionByIdOnce(id: Long) = liveData {
        emit(repo.getBatchProductConsumptionByIdOnce(id))
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

    fun updateBatch(batch: ProductionBatch) = viewModelScope.launch {
        repo.updateBatch(batch)
    }

    fun deleteAll() = viewModelScope.launch {
        repo.deleteAll()
    }

    suspend fun getAverageProductionCost(productId: Long): Double {
        return repo.getAverageProductionCost(productId)
    }
}