package com.rige.dulcegest.data.local.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.rige.dulcegest.data.local.entities.ShoppingListItem
import com.rige.dulcegest.data.local.entities.Supply

data class ShoppingListItemWithSupply(
    @Embedded val item: ShoppingListItem,
    @Relation(
        parentColumn = "supply_id",
        entityColumn = "id"
    )
    val supply: Supply
)
