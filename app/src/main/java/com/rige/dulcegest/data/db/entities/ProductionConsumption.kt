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
            entity = Ingredient::class,
            parentColumns = ["id"],
            childColumns = ["ingredient_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["batch_id"]),
        Index(value = ["ingredient_id"]),
        Index(value = ["batch_id", "ingredient_id"], unique = true)
    ]
)
data class ProductionConsumption(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "batch_id")
    val batchId: Long,

    @ColumnInfo(name = "ingredient_id")
    val ingredientId: Long,

    @ColumnInfo(name = "qty_used")
    val qtyUsed: Double,

    val cost: Double
)
