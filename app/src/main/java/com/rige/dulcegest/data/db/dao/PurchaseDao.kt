package com.rige.dulcegest.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rige.dulcegest.data.db.entities.Purchase

@Dao
interface PurchaseDao {

    @Query("SELECT * FROM purchases ORDER BY date DESC")
    fun getAll(): LiveData<List<Purchase>>

    @Query("SELECT * FROM purchases WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Purchase?

    @Insert
    suspend fun insert(purchase: Purchase): Long

    @Query("""
        SELECT * FROM purchases 
        WHERE supply_id = :supplyId 
        ORDER BY date DESC
    """)
    suspend fun getBySupply(supplyId: Long): List<Purchase>

    @Query("DELETE FROM purchases WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("""
    SELECT IFNULL(SUM(total_price), 0) 
    FROM purchases
    WHERE date >= date('now', '-7 day')
""")
    fun getTotalThisWeek(): LiveData<Double>
}
