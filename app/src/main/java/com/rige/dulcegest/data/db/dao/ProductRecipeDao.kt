package com.rige.dulcegest.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rige.dulcegest.data.db.entities.ProductRecipe
import com.rige.dulcegest.data.db.relations.ProductRecipeWithIngredient

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
    suspend fun deleteByProduct(productId: Long)

    @Transaction
    @Query("SELECT * FROM product_recipes WHERE product_id = :productId")
    fun getRecipeWithIngredients(productId: Long): LiveData<List<ProductRecipeWithIngredient>>
}
