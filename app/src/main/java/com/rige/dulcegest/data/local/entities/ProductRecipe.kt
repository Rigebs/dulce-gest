package com.rige.dulcegest.data.local.entities

import androidx.room.*

@Entity(
    tableName = "product_recipes",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Supply::class,
            parentColumns = ["id"],
            childColumns = ["supply_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["product_id"]),
        Index(value = ["supply_id"]),
        Index(value = ["product_id", "supply_id"], unique = true)
    ]
)
data class ProductRecipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "product_id")
    val productId: Long,

    @ColumnInfo(name = "supply_id")
    val supplyId: Long,

    @ColumnInfo(name = "qty_per_unit")
    val qtyPerUnit: Double? = null
)
