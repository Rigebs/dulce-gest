package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.db.dao.IngredientDao
import com.rige.dulcegest.data.db.entities.Ingredient
import jakarta.inject.Inject

class IngredientRepository @Inject constructor(
    private val dao: IngredientDao
) {
    val allIngredients: LiveData<List<Ingredient>> = dao.getAll()

    fun getById(id: Long): LiveData<Ingredient?> = dao.getById(id)

    suspend fun insert(ingredient: Ingredient): Long = dao.insert(ingredient)

    suspend fun update(ingredient: Ingredient) = dao.update(ingredient)

    suspend fun delete(ingredient: Ingredient) = dao.delete(ingredient)

    suspend fun addStock(id: Long, qty: Double) = dao.addStock(id, qty)

    suspend fun consumeStock(id: Long, qty: Double) = dao.consumeStock(id, qty)

    fun getLowStock(threshold: Double = 5.0): LiveData<List<Ingredient>> =
        dao.getLowStock(threshold)
}