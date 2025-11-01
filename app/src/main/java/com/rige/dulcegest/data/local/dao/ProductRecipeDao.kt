package com.rige.dulcegest.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rige.dulcegest.data.local.entities.ProductRecipe
import com.rige.dulcegest.data.local.entities.relations.ProductRecipeWithSupply

@Dao
interface ProductRecipeDao {

    @Query("SELECT * FROM product_recipes WHERE product_id = :productId")
    suspend fun getByProduct(productId: Long): List<ProductRecipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: ProductRecipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<ProductRecipe>)

    @Delete
    suspend fun delete(recipe: ProductRecipe)

    @Query("DELETE FROM product_recipes WHERE product_id = :productId")
    suspend fun deleteByProductId(productId: Long)

    @Transaction
    @Query("SELECT * FROM product_recipes WHERE product_id = :productId")
    fun getRecipeWithSupplies(productId: Long): LiveData<List<ProductRecipeWithSupply>>
}
