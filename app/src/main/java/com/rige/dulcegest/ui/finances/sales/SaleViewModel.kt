package com.rige.dulcegest.ui.finances.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.rige.dulcegest.data.local.entities.Sale
import com.rige.dulcegest.data.local.entities.SaleItem
import com.rige.dulcegest.data.repository.SaleRepository
import com.rige.dulcegest.domain.usecases.finances.sales.GetTotalSalesForCurrentWeekUseCase
import com.rige.dulcegest.domain.usecases.finances.sales.GetTotalSalesTodayUseCase
import com.rige.dulcegest.domain.usecases.finances.sales.RegisterSaleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SaleViewModel @Inject constructor(
    private val repo: SaleRepository,
    private val registerSaleUseCase: RegisterSaleUseCase,
    private val getTotalSalesTodayUseCase: GetTotalSalesTodayUseCase,
    private val getTotalSalesForCurrentWeekUseCase: GetTotalSalesForCurrentWeekUseCase
) : ViewModel() {

    val sales = repo.allSales

    suspend fun insertSale(sale: Sale, items: List<SaleItem>) {
        registerSaleUseCase.execute(sale, items)
    }

    fun getSaleWithItems(id: Long) = liveData {
        emit(repo.getSaleWithItems(id))
    }

    fun deleteSale(sale: Sale) = viewModelScope.launch {
        repo.deleteSale(sale)
    }

    fun getTotalSalesToday() = getTotalSalesTodayUseCase.execute()

    fun getTotalSalesThisWeek() = getTotalSalesForCurrentWeekUseCase.execute()
}