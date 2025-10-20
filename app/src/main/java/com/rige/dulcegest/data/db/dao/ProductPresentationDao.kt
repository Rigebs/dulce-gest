package com.rige.dulcegest.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rige.dulcegest.data.db.entities.ProductPresentation

@Dao
interface ProductPresentationDao {

    @Query("SELECT * FROM product_presentations WHERE product_id = :productId")
    fun getByProductId(productId: Long): LiveData<List<ProductPresentation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(presentation: ProductPresentation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(presentations: List<ProductPresentation>)

    @Query("DELETE FROM product_presentations WHERE product_id = :productId")
    suspend fun deleteByProductId(productId: Long)
}