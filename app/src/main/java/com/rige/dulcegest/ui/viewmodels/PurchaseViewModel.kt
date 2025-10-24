package com.rige.dulcegest.ui.viewmodels

import androidx.lifecycle.*
import com.rige.dulcegest.data.db.entities.Purchase
import com.rige.dulcegest.data.repository.PurchaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    private val repo: PurchaseRepository
) : ViewModel() {

    val purchases = repo.allPurchases

    fun insert(purchase: Purchase) = viewModelScope.launch {
        repo.insert(purchase)
    }

    fun getTotalPurchasesThisWeek(): LiveData<Double> = repo.getTotalPurchasesThisWeek()
}