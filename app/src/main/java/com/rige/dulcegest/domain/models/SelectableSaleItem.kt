package com.rige.dulcegest.domain.models

data class SelectableSaleItem(
    val productId: Long,
    val presentationId: Long? = null,
    val name: String,
    val price: Double,
    val presentationQuantity: Double = 1.0
)