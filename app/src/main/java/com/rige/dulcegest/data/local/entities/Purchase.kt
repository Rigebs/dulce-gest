package com.rige.dulcegest.data.local.entities

import androidx.room.*

@Entity(
    tableName = "purchases",
    foreignKeys = [
        ForeignKey(
            entity = Supply::class,
            parentColumns = ["id"],
            childColumns = ["supply_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["supply_id"])]
)
data class Purchase(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "supply_id")
    val supplyId: Long,

    val quantity: Double,

    @ColumnInfo(name = "total_price")
    val totalPrice: Double,

    val supplier: String? = null,
    val date: String? = null,
    val notes: String? = null
)
