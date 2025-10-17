package com.rige.dulcegest.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rige.dulcegest.data.db.entities.ProductionBatch
import com.rige.dulcegest.data.db.relations.BatchWithConsumptions

@Dao
interface ProductionBatchDao {

    @Query("SELECT * FROM production_batches ORDER BY date DESC")
    fun getAll(): LiveData<List<ProductionBatch>>

    @Query("SELECT * FROM production_batches WHERE id = :id")
    suspend fun getById(id: Long): ProductionBatch?

    @Insert
    suspend fun insert(batch: ProductionBatch): Long

    @Update
    suspend fun update(batch: ProductionBatch)

    @Delete
    suspend fun delete(batch: ProductionBatch)

    @Transaction
    @Query("SELECT * FROM production_batches WHERE id = :id")
    suspend fun getBatchWithConsumptions(id: Long): BatchWithConsumptions?
}