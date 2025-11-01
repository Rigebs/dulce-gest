package com.rige.dulcegest.ui.products.productions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private val saveProductionUseCase: SaveProductionUseCase, // 👈 Nuevo
    private val updateProductionBatchUseCase: UpdateProductionBatchUseCase // 👈 Nuevo
) : ViewModel() {

    // -------------------------------------------------------------------------
    // DATOS EXPUESTOS A LA UI (LISTAS)
    // -------------------------------------------------------------------------

    // Se mantiene para la lista principal (ProductionListFragment)
    val batchesWithProductAndConsumptions = repo.allFullBatches

    // LiveData para manejar el resultado del guardado (éxito/error)
    private val _saveResult = MutableLiveData<SaveProductionUseCase.Result>()
    val saveResult: LiveData<SaveProductionUseCase.Result> = _saveResult

    // -------------------------------------------------------------------------
    // OPERACIONES DE NEGOCIO (USANDO USE CASES)
    // -------------------------------------------------------------------------

    /**
     * Llama al Use Case para registrar la producción completa (validación, cálculo, transacción).
     */
    fun registerNewProduction(
        selectedProduct: Product,
        qtyProduced: Double,
        supplyUsages: Map<Long, Double>,
        notes: String?
    ) = viewModelScope.launch {
        // El Use Case hace TODO: Validación, Cálculo de Costos, y llama a la Transacción del Repository
        val result = saveProductionUseCase.execute(
            selectedProduct,
            qtyProduced,
            supplyUsages,
            notes
        )
        _saveResult.postValue(result)
    }

    /**
     * Llama al Use Case para actualizar solo la cantidad de un lote existente.
     */
    fun updateBatchQuantity(
        batchId: Long,
        newQty: Double
    ) = viewModelScope.launch {
        // El Use Case/Repository hace la lógica de recalcular consumos/costos y actualizar el stock.
        updateProductionBatchUseCase.execute(batchId, newQty)
    }


    // -------------------------------------------------------------------------
    // OPERACIONES CRUD SIMPLES / OBTENCIÓN DE DATOS
    // -------------------------------------------------------------------------

    fun deleteBatch(batch: ProductionBatch) = viewModelScope.launch {
        repo.deleteBatch(batch)
    }

    // Se utiliza para el deshacer de la eliminación. Requiere el lote y los consumos
    fun reinsertBatchTransaction(batch: ProductionBatch, consumptions: List<ProductionConsumption>) =
        viewModelScope.launch {
            // Nota: Se debe verificar que esta inserción maneje el reajuste de stock si es necesario.
            // Para simplificar, si la transacción completa (incluyendo reajustes de stock) se borró,
            // esta re-inserción debería ser la versión atómica que la restaura.
            // Usamos la versión atómica del repositorio para la re-inserción.
            repo.saveNewBatchTransaction(batch, consumptions)
        }

    fun getBatchProductConsumptionByIdOnce(id: Long) = liveData {
        emit(repo.getBatchProductConsumptionByIdOnce(id))
    }

    // Se mantienen si son usados por otros ViewModels/Fragments de reportes
    suspend fun getAverageProductionCost(productId: Long): Double {
        return repo.getAverageProductionCost(productId)
    }

}