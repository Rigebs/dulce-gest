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
    /**
     * Calcula la ganancia neta de la semana procesando las ventas, restando el costo de producción
     * de los productos vendidos, y finalmente restando los gastos generales semanales.
     *
     * @param salesOfThisWeek Lista de ventas completas de la semana actual.
     * @return El valor de la Ganancia Neta de la semana.
     */
    suspend fun execute(salesOfThisWeek: List<SaleWithItems>): Double {
        var totalGrossProfit = 0.0

        for (sale in salesOfThisWeek) {
            for (item in sale.items) {
                // 1. Obtener el Producto
                val product = productRepo.getProductByIdSuspend(item.productId) // Asume que ProductRepository tiene este método

                if (product != null) {
                    // 2. Obtener el Costo de Producción Promedio
                    // Esta llamada es la que consume más tiempo (IO)
                    val unitCost = productionRepo.getAverageProductionCost(product.id) // Asume este método en ProductionRepository

                    // 3. Cálculo de Costo y Ganancia Bruta por ítem
                    // Cantidad vendida en unidades base: qty * factor de conversión a base
                    val presentationQuantity = item.presentationQuantity ?: 1.0
                    val totalCost = unitCost * presentationQuantity * item.qty

                    // Ganancia bruta = (Precio de venta * Cantidad vendida) - Costo total
                    val grossProfitPerItem = (item.unitPrice * item.qty) - totalCost
                    totalGrossProfit += grossProfitPerItem
                }
            }
        }

        // 4. Obtener Gastos Semanales
        val weeklyExpenses = expenseRepo.getTotalExpensesThisWeekSuspend() ?: 0.0

        // 5. Cálculo de Ganancia Neta
        val netProfit = totalGrossProfit - weeklyExpenses

        // 6. Redondear el resultado
        return round(netProfit * 100) / 100.0
    }
}