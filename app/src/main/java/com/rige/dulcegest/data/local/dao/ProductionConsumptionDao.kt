package com.rige.dulcegest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rige.dulcegest.data.local.entities.ProductionConsumption

@Dao
interface ProductionConsumptionDao {

    @Query("SELECT * FROM production_consumptions WHERE batch_id = :batchId")
    suspend fun getByBatch(batchId: Long): List<ProductionConsumption>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(consumption: ProductionConsumption): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(consumptions: List<ProductionConsumption>)

    @Query("DELETE FROM production_consumptions WHERE batch_id = :batchId")
    suspend fun deleteByBatchId(batchId: Long)
}
