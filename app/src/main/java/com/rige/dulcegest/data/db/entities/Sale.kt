package com.rige.dulcegest.data.db.entities

import androidx.room.*

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "sale_date")
    val saleDate: String? = null,

    val customer: String? = null,

    @ColumnInfo(name = "total_amount")
    val totalAmount: Double = 0.0,

    val notes: String? = null
)
