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

    private val salesOfThisWeek: LiveData<List<SaleWithItems>> = saleRepo.salesOfThisWeek

    val lowStockSupplies: LiveData<List<Supply>> = supplyRepo.getLowStock()

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