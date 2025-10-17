package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.db.dao.IngredientDao
import com.rige.dulcegest.data.db.dao.ProductDao
import com.rige.dulcegest.data.db.dao.ProductRecipeDao
import com.rige.dulcegest.data.db.entities.Ingredient
import com.rige.dulcegest.data.db.entities.Product
import com.rige.dulcegest.data.db.entities.ProductRecipe
import com.rige.dulcegest.data.db.relations.ProductRecipeWithIngredient
import jakarta.inject.Inject

class ProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val recipeDao: ProductRecipeDao,
    private val productRecipeDao: ProductRecipeDao
) {
    val allProducts: LiveData<List<Product>> = productDao.getAll()

    fun getById(id: Long): LiveData<Product?> = productDao.getById(id)

    suspend fun insert(product: Product): Long = productDao.insert(product)

    suspend fun update(product: Product) = productDao.update(product)

    suspend fun delete(product: Product) = productDao.delete(product)

    suspend fun getRecipe(productId: Long): List<ProductRecipe> = recipeDao.getByProduct(productId)

    fun getRecipeWithIngredients(productId: Long): LiveData<List<ProductRecipeWithIngredient>> {
        return productRecipeDao.getRecipeWithIngredients(productId)
    }

    suspend fun setRecipe(productId: Long, ingredients: List<ProductRecipe>) {
        recipeDao.deleteByProduct(productId)
        recipeDao.insertAll(ingredients)
    }

    suspend fun adjustStock(id: Long, qtyDelta: Double) {
        if (qtyDelta > 0) productDao.addStock(id, qtyDelta)
        else productDao.reduceStock(id, -qtyDelta)
    }

    suspend fun calculateProductCost(productId: Long, ingredients: List<Ingredient>): Double {
        val recipe = recipeDao.getByProduct(productId)
        var totalCost = 0.0
        recipe.forEach { item ->
            val ingredient = ingredients.find { it.id == item.ingredientId }
            if (ingredient != null) {
                totalCost += item.qtyPerUnit * ingredient.costPerUnit
            }
        }
        return totalCost
    }

    suspend fun consumeIngredientsForProduct(productId: Long, qty: Double, ingredientDao: IngredientDao) {
        val recipe = recipeDao.getByProduct(productId)
        recipe.forEach { item ->
            val totalQty = item.qtyPerUnit * qty
            ingredientDao.consumeStock(item.ingredientId, totalQty)
        }
    }
}