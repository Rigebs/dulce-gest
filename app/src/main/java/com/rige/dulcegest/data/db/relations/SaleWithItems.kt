package com.rige.dulcegest.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.rige.dulcegest.data.db.entities.Sale
import com.rige.dulcegest.data.db.entities.SaleItem

data class SaleWithItems(
    @Embedded val sale: Sale,
    @Relation(
        parentColumn = "id",
        entityColumn = "sale_id",
        entity = SaleItem::class
    )
    val items: List<SaleItem>
)