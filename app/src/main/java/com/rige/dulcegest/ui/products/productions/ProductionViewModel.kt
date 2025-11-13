package com.rige.dulcegest.ui.products.productions

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.rige.dulcegest.data.local.entities.Product
import com.rige.dulcegest.data.local.entities.ProductionBatch
import com.rige.dulcegest.data.local.entities.ProductionConsumption
import com.rige.dulcegest.data.repository.ProductionRepository
import com.rige.dulcegest.domain.usecases.products.productions.SaveProductionUseCase
import com.rige.dulcegest.domain.usecases.products.productions.UpdateProductionBatchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ProductionViewModel @Inject constructor(
    private val repo: ProductionRepository,
    private val saveProductionUseCase: SaveProductionUseCase,
    private val updateProductionBatchUseCase: UpdateProductionBatchUseCase
) : ViewModel() {

    val batchesWithProductAndConsumptions = repo.allFullBatches

    fun registerNewProduction(
        selectedProduct: Product,
        qtyProduced: Double,
        supplyUsages: Map<Long, Double>,
        notes: String?
    ): LiveData<SaveProductionUseCase.Result> = liveData {
        val result = saveProductionUseCase.execute(
            selectedProduct,
            qtyProduced,
            supplyUsages,
            notes
        )
        emit(result)
    }

    fun updateBatchQuantity(
        batchId: Long,
        newQty: Double
    ) = viewModelScope.launch {
        updateProductionBatchUseCase.execute(batchId, newQty)
    }

    fun deleteBatch(batch: ProductionBatch) = viewModelScope.launch {
        repo.deleteBatch(batch)
    }

    fun reinsertBatchTransaction(batch: ProductionBatch, consumptions: List<ProductionConsumption>) =
        viewModelScope.launch {
            repo.saveNewBatchTransaction(batch, consumptions)
        }

    fun getBatchProductConsumptionByIdOnce(id: Long) = liveData {
        emit(repo.getBatchProductConsumptionByIdOnce(id))
    }
}