package com.rige.dulcegest.ui.finances.sales

import androidx.lifecycle.*
import com.rige.dulcegest.data.local.entities.Sale
import com.rige.dulcegest.data.local.entities.SaleItem
import com.rige.dulcegest.data.repository.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SaleViewModel @Inject constructor(
    private val repo: SaleRepository
) : ViewModel() {

    val sales = repo.allSales

    suspend fun insertSale(sale: Sale, items: List<SaleItem>) {
        repo.insertSale(sale, items)
    }

    fun getSaleWithItems(id: Long) = liveData {
        emit(repo.getSaleWithItems(id))
    }

    fun deleteSale(sale: Sale) = viewModelScope.launch {
        repo.deleteSale(sale)
    }

    fun getTotalSalesToday() = repo.getTotalSalesToday()
    fun getTotalSalesThisWeek() = repo.getTotalSalesThisWeek()

    fun deleteAll() = viewModelScope.launch {
        repo.deleteAll()
    }
}
