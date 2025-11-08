package com.rige.dulcegest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rige.dulcegest.data.local.entities.SaleItem

@Dao
interface SaleItemDao {

    @Query("SELECT * FROM sale_items WHERE sale_id = :saleId")
    suspend fun getBySale(saleId: Long): List<SaleItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SaleItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SaleItem>)

    @Query("DELETE FROM sale_items")
    suspend fun deleteAllSaleItems()
}
