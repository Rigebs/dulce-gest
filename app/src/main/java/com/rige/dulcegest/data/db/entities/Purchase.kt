package com.rige.dulcegest.data.db.entities

import androidx.room.*

@Entity(
    tableName = "purchases",
    foreignKeys = [
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = ["id"],
            childColumns = ["ingredient_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["ingredient_id"])]
)
data class Purchase(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "ingredient_id")
    val ingredientId: Long,

    val quantity: Double,

    @ColumnInfo(name = "unit_price")
    val unitPrice: Double,

    @ColumnInfo(name = "total_price")
    val totalPrice: Double = quantity * unitPrice,

    val supplier: String? = null,

    val date: String? = null,

    val notes: String? = null
)
