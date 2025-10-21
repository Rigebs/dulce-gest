package com.rige.dulcegest.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rige.dulcegest.data.db.entities.Product
import com.rige.dulcegest.data.db.relations.ProductWithPresentations
import com.rige.dulcegest.data.db.relations.ProductWithPresentationsAndVariants
import com.rige.dulcegest.data.db.relations.ProductWithVariants

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAll(): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun getById(id: Long): LiveData<Product?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product): Long

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("UPDATE products SET stock_qty = stock_qty + :qty WHERE id = :id")
    suspend fun addStock(id: Long, qty: Double)

    @Query("UPDATE products SET stock_qty = stock_qty - :qty WHERE id = :id")
    suspend fun reduceStock(id: Long, qty: Double)

    @Transaction
    @Query("SELECT * FROM products")
    fun getProductsWithPresentations(): LiveData<List<ProductWithPresentations>>

    @Transaction
    @Query("SELECT * FROM products")
    fun getProductsWithVariants(): LiveData<List<ProductWithVariants>>

    @Transaction
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getProductsWithPresentationsAndVariants(): LiveData<List<ProductWithPresentationsAndVariants>>
}
