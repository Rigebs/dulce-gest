package com.rige.dulcegest.data.db.entities

import androidx.room.*

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val date: String? = null,
    val category: String,
    val amount: Double,
    val notes: String? = null
)
