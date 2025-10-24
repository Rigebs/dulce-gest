package com.rige.dulcegest.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rige.dulcegest.data.db.entities.Supply
import com.rige.dulcegest.data.db.entities.Sale
import com.rige.dulcegest.data.repository.SupplyRepository
import com.rige.dulcegest.data.repository.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val saleRepo: SaleRepository,
    private val supplyRepo: SupplyRepository
) : ViewModel() {

    val recentSales: LiveData<List<Sale>> = saleRepo.lastFiveSales

    val lowStockSupplies: LiveData<List<Supply>> = supplyRepo.getLowStock()
}
