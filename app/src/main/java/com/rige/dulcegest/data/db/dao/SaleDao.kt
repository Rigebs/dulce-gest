package com.rige.dulcegest.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rige.dulcegest.data.db.entities.Sale
import com.rige.dulcegest.data.db.relations.SaleWithItems

@Dao
interface SaleDao {

    @Query("SELECT * FROM sales ORDER BY sale_date DESC")
    fun getAll(): LiveData<List<Sale>>

    @Insert
    suspend fun insert(sale: Sale): Long

    @Update
    suspend fun update(sale: Sale)

    @Delete
    suspend fun delete(sale: Sale)

    @Transaction
    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSaleWithItems(id: Long): SaleWithItems?
}
