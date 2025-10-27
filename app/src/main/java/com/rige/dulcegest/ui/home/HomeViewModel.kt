package com.rige.dulcegest.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rige.dulcegest.data.local.entities.Supply
import com.rige.dulcegest.data.local.entities.relations.SaleWithItems
import com.rige.dulcegest.data.repository.SaleRepository
import com.rige.dulcegest.data.repository.SupplyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val saleRepo: SaleRepository,
    private val supplyRepo: SupplyRepository
) : ViewModel() {

    val recentSales: LiveData<List<SaleWithItems>> = saleRepo.lastFiveSales

    val salesOfThisWeek: LiveData<List<SaleWithItems>> = saleRepo.salesOfThisWeek

    val lowStockSupplies: LiveData<List<Supply>> = supplyRepo.getLowStock()
}
