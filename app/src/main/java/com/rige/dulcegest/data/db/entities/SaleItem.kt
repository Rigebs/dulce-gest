package com.rige.dulcegest.data.db.entities

import androidx.room.*

@Entity(
    tableName = "sale_items",
    foreignKeys = [
        ForeignKey(
            entity = Sale::class,
            parentColumns = ["id"],
            childColumns = ["sale_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(entity = ProductPresentation::class,
            parentColumns = ["id"],
            childColumns = ["presentation_id"],
            onDelete = ForeignKey.NO_ACTION)

    ],
    indices = [
        Index(value = ["sale_id"]),
        Index(value = ["product_id"]),
        Index(value = ["sale_id", "product_id"], unique = true),
        Index(value = ["presentation_id"])
    ]
)
data class SaleItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "sale_id")
    val saleId: Long,

    @ColumnInfo(name = "product_id")
    val productId: Long,

    @ColumnInfo(name = "presentation_id")
    val presentationId: Long?,

    val qty: Double,

    @ColumnInfo(name = "unit_price")
    val unitPrice: Double,

    @ColumnInfo(name = "line_total")
    val lineTotal: Double = qty * unitPrice,

    val presentationQuantity: Double = 1.0
)
