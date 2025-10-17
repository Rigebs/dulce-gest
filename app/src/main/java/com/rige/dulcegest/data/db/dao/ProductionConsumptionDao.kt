package com.rige.dulcegest.data.db.dao

import androidx.room.*
import com.rige.dulcegest.data.db.entities.ProductionConsumption

@Dao
interface ProductionConsumptionDao {

    @Query("SELECT * FROM production_consumptions WHERE batch_id = :batchId")
    suspend fun getByBatch(batchId: Long): List<ProductionConsumption>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(consumption: ProductionConsumption): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(consumptions: List<ProductionConsumption>)

    @Query("DELETE FROM production_consumptions WHERE batch_id = :batchId")
    suspend fun deleteByBatch(batchId: Long)
}
