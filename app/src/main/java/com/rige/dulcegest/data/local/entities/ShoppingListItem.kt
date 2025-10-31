package com.rige.dulcegest.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_list_items",
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
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "supply_id")
    val supplyId: Long,

    val quantity: Double? = null,
    val unit: String? = null,

    val priority: Int = 0,

    val purchased: Boolean = false,

    val notes: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String? = null
)