package com.rige.dulcegest.data.db.entities

import androidx.room.*

@Entity(
    tableName = "products",
    indices = [Index(value = ["name"], unique = true)]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String,
    val unit: String = "u",
    val price: Double = 0.0,

    @ColumnInfo(name = "stock_qty")
    val stockQty: Double = 0.0,

    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,

    @ColumnInfo(name = "image_path")
    val imagePath: String? = null,

    val notes: String? = null
)
