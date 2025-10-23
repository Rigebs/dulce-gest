package com.rige.dulcegest.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rige.dulcegest.data.db.entities.Ingredient

@Dao
interface IngredientDao {

    @Query("SELECT * FROM ingredients ORDER BY name ASC")
    fun getAll(): LiveData<List<Ingredient>>

    @Query("SELECT * FROM ingredients WHERE id = :id LIMIT 1")
    fun getById(id: Long): LiveData<Ingredient?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ingredient: Ingredient): Long

    @Update
    suspend fun update(ingredient: Ingredient)

    @Delete
    suspend fun delete(ingredient: Ingredient)

    @Query("UPDATE ingredients SET stock_qty = stock_qty + :qty WHERE id = :id")
    suspend fun addStock(id: Long, qty: Double)

    @Query("UPDATE ingredients SET stock_qty = stock_qty - :qty WHERE id = :id")
    suspend fun consumeStock(id: Long, qty: Double)

    @Query("SELECT * FROM ingredients WHERE stock_qty <= :threshold ORDER BY stock_qty ASC")
    fun getLowStock(threshold: Double = 5.0): LiveData<List<Ingredient>>
}