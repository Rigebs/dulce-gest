package com.rige.dulcegest.domain.models

data class DailySalesSummary(
    val dateOrPeriodLabel: String,
    val totalSales: Double,
    val date: String
)