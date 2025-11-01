package com.rige.dulcegest.ui.finances.purchases

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rige.dulcegest.data.local.entities.Supply
import com.rige.dulcegest.data.repository.PurchaseRepository
import com.rige.dulcegest.domain.usecases.finances.purchases.RegisterSupplyPurchaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    private val repo: PurchaseRepository,
    private val registerSupplyPurchaseUseCase: RegisterSupplyPurchaseUseCase
) : ViewModel() {

    val purchases = repo.allPurchases

    fun registerPurchase(
        selectedSupply: Supply,
        quantity: Double,
        totalPrice: Double,
        supplier: String?,
        notes: String?
    ) = viewModelScope.launch {
        registerSupplyPurchaseUseCase.execute(
            selectedSupply,
            quantity,
            totalPrice,
            supplier,
            notes
        )
    }

    fun getTotalPurchasesThisWeek(): LiveData<Double> = repo.getTotalPurchasesThisWeek()
}