package com.rige.dulcegest.data.local.entities

import androidx.room.*

@Entity(
    tableName = "production_batches",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["product_id"])]
)
data class ProductionBatch(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "product_id")
    val productId: Long,

    @ColumnInfo(name = "quantity_produced")
    val quantityProduced: Double,

    @ColumnInfo(name = "total_cost")
    val totalCost: Double = 0.0,

    val date: String? = null,
    val notes: String? = null
)
