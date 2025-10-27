package com.rige.dulcegest.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.rige.dulcegest.data.local.entities.ProductionBatch
import com.rige.dulcegest.data.local.entities.relations.BatchWithConsumptions
import com.rige.dulcegest.data.local.entities.relations.ProductionBatchWithProduct
import com.rige.dulcegest.data.local.entities.relations.ProductionBatchWithProductAndConsumptions

@Dao
interface ProductionBatchDao {

    @Query("SELECT * FROM production_batches ORDER BY date DESC")
    fun getAll(): LiveData<List<ProductionBatch>>

    @Transaction
    @Query("SELECT * FROM production_batches ORDER BY date DESC")
    fun getBatchesWithProduct(): LiveData<List<ProductionBatchWithProduct>>

    @Transaction
    @Query("SELECT * FROM production_batches ORDER BY date DESC")
    fun getFullBatches(): LiveData<List<ProductionBatchWithProductAndConsumptions>>

    @Transaction
    @Query("SELECT * FROM production_batches WHERE id = :id")
    suspend fun getById(id: Long): ProductionBatchWithProduct?

    @Transaction
    @Query("SELECT * FROM production_batches WHERE id = :id")
    suspend fun getBatchProductConsumptionByIdOnce(id: Long): ProductionBatchWithProductAndConsumptions?

    @Insert
    suspend fun insert(batch: ProductionBatch): Long

    @Update
    suspend fun update(batch: ProductionBatch)

    @Delete
    suspend fun delete(batch: ProductionBatch)

    @Transaction
    @Query("SELECT * FROM production_batches WHERE id = :id")
    suspend fun getBatchWithConsumptions(id: Long): BatchWithConsumptions?

    @Query("DELETE FROM production_batches")
    suspend fun deleteAllProductions()

    @Query("SELECT * FROM production_batches WHERE product_id = :productId AND date BETWEEN :startDate AND :endDate")
    suspend fun getBatchesForProductInPeriod(
        productId: Long,
        startDate: String,
        endDate: String
    ): List<ProductionBatch>

    @Query("SELECT SUM(total_cost) / SUM(quantity_produced) FROM production_batches WHERE product_id = :productId")
    suspend fun getAverageProductionCost(productId: Long): Double?
}