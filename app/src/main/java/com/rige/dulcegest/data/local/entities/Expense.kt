package com.rige.dulcegest.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val date: String? = null,
    val category: String,
    val amount: Double,
    val notes: String? = null
)
