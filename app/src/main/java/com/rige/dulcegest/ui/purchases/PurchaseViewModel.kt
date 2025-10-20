package com.rige.dulcegest.ui.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rige.dulcegest.data.db.entities.Purchase
import com.rige.dulcegest.data.repository.PurchaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    private val repository: PurchaseRepository
) : ViewModel() {

    val allPurchases = repository.allPurchases

    fun insert(purchase: Purchase) = viewModelScope.launch {
        repository.insert(purchase)
    }
}
