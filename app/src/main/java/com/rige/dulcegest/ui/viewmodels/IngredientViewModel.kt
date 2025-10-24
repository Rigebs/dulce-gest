package com.rige.dulcegest.ui.viewmodels

import androidx.lifecycle.*
import com.rige.dulcegest.data.db.entities.Ingredient
import com.rige.dulcegest.data.repository.IngredientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class IngredientViewModel @Inject constructor(
    private val repo: IngredientRepository
) : ViewModel() {

    val ingredients = repo.allIngredients

    fun getIngredientById(id: Long): LiveData<Ingredient?> = repo.getById(id)

    fun insert(ingredient: Ingredient) = viewModelScope.launch { repo.insert(ingredient) }
    fun update(ingredient: Ingredient) = viewModelScope.launch { repo.update(ingredient) }
    fun delete(ingredient: Ingredient) = viewModelScope.launch { repo.delete(ingredient) }
    fun addStock(id: Long, qty: Double) = viewModelScope.launch { repo.addStock(id, qty) }
    fun consumeStock(id: Long, qty: Double) = viewModelScope.launch { repo.consumeStock(id, qty) }

    suspend fun getAllIngredientsOnce(): List<Ingredient> {
        return repo.getAllOnce()
    }
}