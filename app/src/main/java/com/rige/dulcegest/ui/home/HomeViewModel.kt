package com.rige.dulcegest.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rige.dulcegest.data.db.entities.Ingredient
import com.rige.dulcegest.data.db.entities.Sale
import com.rige.dulcegest.data.repository.IngredientRepository
import com.rige.dulcegest.data.repository.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val saleRepo: SaleRepository,
    private val ingredientRepo: IngredientRepository
) : ViewModel() {

    val recentSales: LiveData<List<Sale>> = saleRepo.lastFiveSales

    val lowStockIngredients: LiveData<List<Ingredient>> = ingredientRepo.getLowStock()
}
