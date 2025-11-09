package com.rige.dulcegest.domain.usecases.home

import com.rige.dulcegest.data.local.entities.relations.SaleWithItems
import com.rige.dulcegest.data.repository.ExpenseRepository
import com.rige.dulcegest.data.repository.ProductRepository
import com.rige.dulcegest.data.repository.ProductionRepository
import dagger.hilt.android.scopes.ViewModelScoped
import jakarta.inject.Inject
import kotlin.math.round

@ViewModelScoped
class CalculateWeeklyNetProfitUseCase @Inject constructor(
    private val productRepo: ProductRepository,
    private val productionRepo: ProductionRepository,
    private val expenseRepo: ExpenseRepository
) {

    suspend fun execute(salesOfThisWeek: List<SaleWithItems>): Double {
        var totalGrossProfit = 0.0

        for (sale in salesOfThisWeek) {
            for (item in sale.items) {

                val product = productRepo.getProductByIdSuspend(item.productId)

                if (product != null) {

                    val unitCost = calculateAverageUnitCost(product.id)

                    val presentationQuantity = item.presentationQuantity

                    val totalCost = unitCost * presentationQuantity * item.qty

                    val grossProfitPerItem = (item.unitPrice * item.qty) - totalCost
                    totalGrossProfit += grossProfitPerItem
                }
            }
        }

        val weeklyExpenses = expenseRepo.getTotalExpensesThisWeekSuspend()

        val netProfit = totalGrossProfit - weeklyExpenses

        return round(netProfit * 100) / 100.0
    }

    private suspend fun calculateAverageUnitCost(productId: Long): Double {

        val lastBatches = productionRepo.getLastFiveBatchesForProduct(productId)

        if (lastBatches.isEmpty()) {
            return 0.0
        }

        val totalCostSum: Double = lastBatches.sumOf { it.totalCost }

        val totalQuantitySum: Double = lastBatches.sumOf { it.quantityProduced }

        if (totalQuantitySum == 0.0) {
            return 0.0
        }
        return totalCostSum / totalQuantitySum
    }
}