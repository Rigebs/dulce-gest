// Contenido de HomeViewModel.kt (Refactorizado)
package com.rige.dulcegest.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.rige.dulcegest.data.local.entities.Supply
import com.rige.dulcegest.data.local.entities.relations.SaleWithItems
import com.rige.dulcegest.data.repository.SaleRepository
import com.rige.dulcegest.data.repository.SupplyRepository
import com.rige.dulcegest.domain.usecases.home.CalculateWeeklyNetProfitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val saleRepo: SaleRepository,
    private val supplyRepo: SupplyRepository,
    private val calculateProfitUseCase: CalculateWeeklyNetProfitUseCase
) : ViewModel() {

    val recentSales: LiveData<List<SaleWithItems>> = saleRepo.lastFiveSales

    // 1. Fuente de datos para el cálculo (Ventas de la semana)
    private val salesOfThisWeek: LiveData<List<SaleWithItems>> = saleRepo.salesOfThisWeek

    val lowStockSupplies: LiveData<List<Supply>> = supplyRepo.getLowStock()

    // 2. 🟢 Nuevo LiveData para la Ganancia Neta calculado usando switchMap
    // Cada vez que 'salesOfThisWeek' cambia, se dispara el liveData builder
    val weeklyNetProfit: LiveData<Double?> = salesOfThisWeek.switchMap { salesList ->
        liveData {
            if (salesList.isNotEmpty()) {
                val profit = calculateProfitUseCase.execute(salesList)
                emit(profit)
            } else {
                emit(0.0)
            }
        }
    }
}