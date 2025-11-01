package com.rige.dulcegest.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rige.dulcegest.data.local.entities.Supply

@Dao
interface SupplyDao {

    @Query("SELECT * FROM supplies ORDER BY name ASC")
    fun getAll(): LiveData<List<Supply>>

    @Query("SELECT * FROM supplies WHERE id = :id LIMIT 1")
    fun getById(id: Long): LiveData<Supply?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(supply: Supply): Long

    @Update
    suspend fun update(supply: Supply)

    @Delete
    suspend fun delete(supply: Supply)

    @Query("""
        UPDATE supplies
        SET 
            stock_qty = stock_qty + :quantity,
            avg_cost = (
                (stock_qty * avg_cost) + (:quantity * (:totalPrice / :quantity))
            ) / (stock_qty + :quantity)
        WHERE id = :id
    """)
    suspend fun updateStockAndCostAfterPurchase(
        id: Long,
        quantity: Double,
        totalPrice: Double
    )

    @Query("UPDATE supplies SET stock_qty = stock_qty - :qty WHERE id = :id")
    suspend fun consumeStock(id: Long, qty: Double)

    @Query("SELECT * FROM supplies WHERE stock_qty <= :threshold ORDER BY stock_qty ASC")
    fun getLowStock(threshold: Double = 5.0): LiveData<List<Supply>>

    @Query("SELECT * FROM supplies")
    suspend fun getAllOnce(): List<Supply>

    @Query("DELETE FROM supplies")
    suspend fun deleteAllSupplies()
}