package com.rige.dulcegest.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.rige.dulcegest.data.db.entities.Product
import com.rige.dulcegest.data.db.entities.SaleItem

data class SaleItemWithProduct(
    @Embedded val item: SaleItem,
    @Relation(
        parentColumn = "product_id",
        entityColumn = "id"
    )
    val product: Product
)
