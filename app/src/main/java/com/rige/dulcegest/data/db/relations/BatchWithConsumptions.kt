package com.rige.dulcegest.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.rige.dulcegest.data.db.entities.ProductionBatch
import com.rige.dulcegest.data.db.entities.ProductionConsumption

data class BatchWithConsumptions(
    @Embedded val batch: ProductionBatch,
    @Relation(
        parentColumn = "id",
        entityColumn = "batch_id",
        entity = ProductionConsumption::class
    )
    val consumptions: List<ProductionConsumption>
)
