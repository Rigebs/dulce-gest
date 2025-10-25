package com.rige.dulcegest.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rige.dulcegest.data.local.entities.ProductVariant

@Dao
interface ProductVariantDao {
    @Query("SELECT * FROM product_variants WHERE product_id = :productId")
    fun getByProductId(productId: Long): LiveData<List<ProductVariant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(variant: ProductVariant): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(variants: List<ProductVariant>)

    @Update
    suspend fun update(variant: ProductVariant)

    @Delete
    suspend fun delete(variant: ProductVariant)

    @Query("DELETE FROM product_variants WHERE product_id = :productId")
    suspend fun deleteByProductId(productId: Long)
}