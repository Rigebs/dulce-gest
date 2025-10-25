package com.rige.dulcegest.data.local.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.rige.dulcegest.data.local.entities.Product
import com.rige.dulcegest.data.local.entities.ProductPresentation
import com.rige.dulcegest.data.local.entities.ProductVariant

data class ProductWithPresentationsAndVariants(
    @Embedded val product: Product,

    @Relation(
        parentColumn = "id",
        entityColumn = "product_id"
    )
    val presentations: List<ProductPresentation> = emptyList(),

    @Relation(
        parentColumn = "id",
        entityColumn = "product_id"
    )
    val variants: List<ProductVariant> = emptyList()
)
