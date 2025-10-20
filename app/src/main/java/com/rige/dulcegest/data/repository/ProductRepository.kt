package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.db.dao.ProductDao
import com.rige.dulcegest.data.db.dao.ProductPresentationDao
import com.rige.dulcegest.data.db.dao.ProductRecipeDao
import com.rige.dulcegest.data.db.entities.Product
import com.rige.dulcegest.data.db.entities.ProductPresentation
import com.rige.dulcegest.data.db.entities.ProductRecipe
import com.rige.dulcegest.data.db.relations.ProductRecipeWithIngredient
import com.rige.dulcegest.data.db.relations.ProductWithPresentations
import jakarta.inject.Inject

class ProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val recipeDao: ProductRecipeDao,
    private val presentationDao: ProductPresentationDao
) {
    val allProducts: LiveData<List<Product>> = productDao.getAll()

    fun getById(id: Long): LiveData<Product?> = productDao.getById(id)

    suspend fun insert(product: Product): Long = productDao.insert(product)

    suspend fun update(product: Product) = productDao.update(product)

    suspend fun delete(product: Product) = productDao.delete(product)

    suspend fun getRecipe(productId: Long): List<ProductRecipe> = recipeDao.getByProduct(productId)

    fun getRecipeWithIngredients(productId: Long): LiveData<List<ProductRecipeWithIngredient>> {
        return recipeDao.getRecipeWithIngredients(productId)
    }

    suspend fun setRecipe(productId: Long, ingredients: List<ProductRecipe>) {
        recipeDao.deleteByProduct(productId)
        recipeDao.insertAll(ingredients)
    }

    suspend fun adjustStock(id: Long, qtyDelta: Double) {
        if (qtyDelta > 0) productDao.addStock(id, qtyDelta)
        else productDao.reduceStock(id, -qtyDelta)
    }

    fun getProductsWithPresentations(): LiveData<List<ProductWithPresentations>> {
        return productDao.getProductsWithPresentations()
    }

    fun getPresentationsByProduct(productId: Long): LiveData<List<ProductPresentation>> {
        return presentationDao.getByProductId(productId)
    }

    suspend fun setPresentations(productId: Long, presentations: List<ProductPresentation>) {
        presentationDao.deleteByProductId(productId)
        if (presentations.isNotEmpty()) {
            presentationDao.insertAll(presentations.map { it.copy(productId = productId) })
        }
    }
}