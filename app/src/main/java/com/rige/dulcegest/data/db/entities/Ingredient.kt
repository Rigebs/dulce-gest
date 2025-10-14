package com.rige.dulcegest.data.db.entities

import androidx.room.*

@Entity(
    tableName = "ingredients",
    indices = [Index(value = ["name"], unique = true)]
)
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String,
    val unit: String,

    @ColumnInfo(name = "cost_per_unit")
    val costPerUnit: Double = 0.0,

    @ColumnInfo(name = "stock_qty")
    val stockQty: Double = 0.0,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null,

    val notes: String? = null
)