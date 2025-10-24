package com.rige.dulcegest.data.db.entities

import androidx.room.*

@Entity(
    tableName = "supplies",
    indices = [Index(value = ["name"], unique = true)]
)
data class Supply(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String,
    val unit: String,

    @ColumnInfo(name = "stock_qty")
    val stockQty: Double = 0.0,

    @ColumnInfo(name = "avg_cost")
    val avgCost: Double = 0.0,

    @ColumnInfo(name = "purchase_unit")
    val purchaseUnit: String? = null,

    @ColumnInfo(name = "conversion_factor")
    val conversionFactor: Double? = null,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null,

    val notes: String? = null
)