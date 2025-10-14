package com.rige.dulcegest.data.db.entities

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
            entity = Ingredient::class,
            parentColumns = ["id"],
            childColumns = ["ingredient_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["product_id"]),
        Index(value = ["ingredient_id"]),
        Index(value = ["product_id", "ingredient_id"], unique = true)
    ]
)
data class ProductRecipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "product_id")
    val productId: Long,

    @ColumnInfo(name = "ingredient_id")
    val ingredientId: Long,

    @ColumnInfo(name = "qty_per_unit")
    val qtyPerUnit: Double
)
