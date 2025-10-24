package com.rige.dulcegest.data.db.entities

import androidx.room.*

@Entity(
    tableName = "production_consumptions",
    foreignKeys = [
        ForeignKey(
            entity = ProductionBatch::class,
            parentColumns = ["id"],
            childColumns = ["batch_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Supply::class,
            parentColumns = ["id"],
            childColumns = ["supply_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["batch_id"]),
        Index(value = ["supply_id"]),
        Index(value = ["batch_id", "supply_id"], unique = true)
    ]
)
data class ProductionConsumption(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "batch_id")
    val batchId: Long,

    @ColumnInfo(name = "supply_id")
    val supplyId: Long,

    @ColumnInfo(name = "qty_used")
    val qtyUsed: Double,

    val cost: Double
)
