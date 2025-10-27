package com.rige.dulcegest.data.local.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.rige.dulcegest.data.local.entities.Product
import com.rige.dulcegest.data.local.entities.ProductionBatch
import com.rige.dulcegest.data.local.entities.ProductionConsumption

data class ProductionBatchWithProductAndConsumptions(
    @Embedded val batch: ProductionBatch,
    @Relation(
        parentColumn = "product_id",
        entityColumn = "id"
    )
    val product: Product,
    @Relation(
        parentColumn = "id",
        entityColumn = "batch_id"
    )
    val consumptions: List<ProductionConsumption>
)
