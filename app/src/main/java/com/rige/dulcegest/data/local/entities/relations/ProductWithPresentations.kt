package com.rige.dulcegest.data.local.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.rige.dulcegest.data.local.entities.Product
import com.rige.dulcegest.data.local.entities.ProductPresentation

data class ProductWithPresentations(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "id",
        entityColumn = "product_id"
    )
    val presentations: List<ProductPresentation>
)
