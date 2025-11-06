package com.rige.dulcegest.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "sale_date")
    val saleDate: String? = null,

    val customer: String? = null,

    @ColumnInfo(name = "total_amount")
    val totalAmount: Double = 0.0,

    @ColumnInfo(name = "payment_method")
    val paymentMethod: String? = null,

    val notes: String? = null
)
