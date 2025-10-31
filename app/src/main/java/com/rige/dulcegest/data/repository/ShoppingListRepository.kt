package com.rige.dulcegest.data.repository

import com.rige.dulcegest.data.local.dao.ShoppingListDao
import com.rige.dulcegest.data.local.entities.ShoppingListItem
import com.rige.dulcegest.data.local.entities.relations.ShoppingListItemWithSupply
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShoppingListRepository @Inject constructor(
    private val dao: ShoppingListDao
) {
    fun getItemsWithSupply(): Flow<List<ShoppingListItemWithSupply>> = dao.getAllWithSupply()

    suspend fun insert(item: ShoppingListItem) = dao.insert(item)
    suspend fun delete(item: ShoppingListItem) = dao.delete(item)
    suspend fun deleteItemBySupplyId(supplyId: Long) = dao.deleteItemBySupplyId(supplyId)
    suspend fun update(item: ShoppingListItem) = dao.update(item)
}