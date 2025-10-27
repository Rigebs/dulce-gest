package com.rige.dulcegest.data.repository

import androidx.lifecycle.LiveData
import com.rige.dulcegest.data.local.dao.ProductDao
import com.rige.dulcegest.data.local.dao.ProductPresentationDao
import com.rige.dulcegest.data.local.dao.ProductRecipeDao
import com.rige.dulcegest.data.local.dao.ProductVariantDao
import com.rige.dulcegest.data.local.entities.Product
import com.rige.dulcegest.data.local.entities.ProductPresentation
import com.rige.dulcegest.data.local.entities.ProductRecipe
import com.rige.dulcegest.data.local.entities.ProductVariant
import com.rige.dulcegest.data.local.entities.relations.ProductRecipeWithSupply
import com.rige.dulcegest.data.local.entities.relations.ProductWithPresentationsAndVariants
import jakarta.inject.Inject

class ProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val recipeDao: ProductRecipeDao,
    private val presentationDao: ProductPresentationDao,
    private val variantDao: ProductVariantDao
) {

    val allProducts: LiveData<List<Product>> = productDao.getAll()

    fun getById(id: Long): LiveData<Product?> = productDao.getById(id)

    suspend fun insert(product: Product): Long = productDao.insert(product)

    suspend fun update(product: Product) = productDao.update(product)

    suspend fun delete(product: Product) = productDao.delete(product)

    suspend fun getProductByIdSuspend(productId: Long): Product? {
        return productDao.getProductByIdSuspend(productId)
    }

    fun getRecipeWithSupplies(productId: Long): LiveData<List<ProductRecipeWithSupply>> =
        recipeDao.getRecipeWithSupplies(productId)

    suspend fun setRecipe(productId: Long, supplies: List<ProductRecipe>) {
        recipeDao.deleteByProduct(productId)
        recipeDao.insertAll(supplies)
    }

    suspend fun adjustStock(id: Long, qtyDelta: Double) {
        println("FROM REPOSITORY Adjusting stock for product $id by $qtyDelta")
        if (qtyDelta > 0) productDao.addStock(id, qtyDelta)
        else productDao.reduceStock(id, -qtyDelta)
    }


    fun getPresentationsByProduct(productId: Long): LiveData<List<ProductPresentation>> =
        presentationDao.getByProductId(productId)

    suspend fun setPresentations(productId: Long, presentations: List<ProductPresentation>) {
        if (presentations.isNotEmpty()) {
            presentationDao.insertAll(presentations.map { it.copy(productId = productId) })
        }
    }

    fun getVariantsByProduct(productId: Long): LiveData<List<ProductVariant>> =
        variantDao.getByProductId(productId)

    suspend fun setVariants(productId: Long, variants: List<ProductVariant>) {
        if (variants.isNotEmpty()) {
            variantDao.insertAll(variants.map { it.copy(productId = productId) })
        }
    }

    fun getProductsWithPresentationsAndVariants(): LiveData<List<ProductWithPresentationsAndVariants>> {
        return productDao.getProductsWithPresentationsAndVariants()
    }

    suspend fun deleteAll() {
        productDao.deleteAllProducts()
    }
}