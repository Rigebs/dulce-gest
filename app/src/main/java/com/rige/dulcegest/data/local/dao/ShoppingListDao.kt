package com.rige.dulcegest.data.local.dao

import androidx.room.*
import com.rige.dulcegest.data.local.entities.ShoppingListItem
import com.rige.dulcegest.data.local.entities.relations.ShoppingListItemWithSupply
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    @Transaction
    @Query("SELECT * FROM shopping_list_items ORDER BY created_at DESC")
    fun getAllWithSupply(): Flow<List<ShoppingListItemWithSupply>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShoppingListItem)

    @Delete
    suspend fun delete(item: ShoppingListItem)

    @Query("DELETE FROM shopping_list_items WHERE supply_id = :supplyId")
    suspend fun deleteItemBySupplyId(supplyId: Long)

    @Update
    suspend fun update(item: ShoppingListItem)
}