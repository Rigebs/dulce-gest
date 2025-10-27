package com.rige.dulcegest.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.R
import com.rige.dulcegest.data.local.entities.relations.SaleWithItems
import com.rige.dulcegest.databinding.FragmentHomeBinding
import com.rige.dulcegest.ui.MainActivity
import com.rige.dulcegest.ui.common.BaseFragment
import com.rige.dulcegest.ui.finances.expenses.ExpenseViewModel
import com.rige.dulcegest.ui.finances.purchases.PurchaseViewModel
import com.rige.dulcegest.ui.finances.sales.SaleViewModel
import com.rige.dulcegest.ui.products.ProductViewModel
import com.rige.dulcegest.ui.products.productions.ProductionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    // ───────────────────────────────
    // 🔹 Configuración general
    // ───────────────────────────────
    override val showToolbar: Boolean = false

    private val saleViewModel: SaleViewModel by viewModels()
    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val purchaseViewModel: PurchaseViewModel by viewModels()
    private val productionViewModel: ProductionViewModel by viewModels()
    private val productViewModel: ProductViewModel by viewModels()

    // ───────────────────────────────
    // 🔹 Ciclo de vida
    // ───────────────────────────────
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupQuickActions()
        setupDailyTip()
    }

    // ───────────────────────────────
    // 🔹 Observadores
    // ───────────────────────────────
    private fun setupObservers() {
        // Ventas recientes
        homeViewModel.recentSales.observe(viewLifecycleOwner) { sales ->
            binding.rvRecentSales.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = RecentSalesAdapter(sales)
            }
        }

        homeViewModel.salesOfThisWeek.observe(viewLifecycleOwner) { sales ->
            updateWeeklyProfit(sales)
        }

        // Ventas semanales
        saleViewModel.getTotalSalesThisWeek().observe(viewLifecycleOwner) { weeklySales ->
            val total = weeklySales ?: 0.0
            binding.txtWeeklySales.text = "S/ %.2f".format(total)
            updateWeeklyGoal(total)
        }

        // Gastos semanales
        expenseViewModel.getTotalExpensesThisWeek().observe(viewLifecycleOwner) { weeklyExpenses ->
            binding.txtWeeklyExpenses.text = "S/ %.2f".format(weeklyExpenses ?: 0.0)
        }

        // Compras semanales
        purchaseViewModel.getTotalPurchasesThisWeek().observe(viewLifecycleOwner) { weeklyPurchases ->
            binding.txtWeeklyPurchases.text = "S/ %.2f".format(weeklyPurchases ?: 0.0)
        }

        // Insumos con poco stock
        homeViewModel.lowStockSupplies.observe(viewLifecycleOwner) { lowStock ->
            if (lowStock.isEmpty()) {
                binding.cardLowStock.visibility = View.GONE
            } else {
                binding.cardLowStock.visibility = View.VISIBLE
                binding.txtLowStock.text =
                    "Insumos críticos: " + lowStock.joinToString(", ") { it.name }
            }
        }
    }

    // ───────────────────────────────
    // 🔹 Cálculo de ganancia semanal
    // ───────────────────────────────
    private fun updateWeeklyProfit(sales: List<SaleWithItems>) {
        lifecycleScope.launch {

            var totalProfit = 0.0

            for (sale in sales) {
                for (item in sale.items) {
                    println(" ITEMS: ${sale.items}")
                    val product = productViewModel.getProductByIdOnce(item.productId)
                    if (product != null) {
                        val unitCost = productionViewModel.getAverageProductionCost(product.id)
                        val totalCost = unitCost * item.presentationQuantity * item.qty
                        val profitPerItem = (item.unitPrice * item.qty) - totalCost
                        totalProfit += profitPerItem
                    }
                }
            }

            val weeklyExpenses = expenseViewModel.getTotalExpensesThisWeek().value ?: 0.0
            val netProfit = totalProfit - weeklyExpenses

            binding.txtWeeklyProfit.text = "S/ %.2f".format(netProfit)
        }
    }

    // ───────────────────────────────
    // 🔹 Objetivo semanal
    // ───────────────────────────────
    private fun updateWeeklyGoal(weeklySales: Double) {
        val goalAmount = 100.0
        val progress = ((weeklySales / goalAmount) * 100).coerceAtMost(100.0)

        binding.progressWeeklyGoal.progress = progress.toInt()
        binding.txtGoalSummary.text =
            "Objetivo: S/ %.2f — Alcanzado: S/ %.2f".format(goalAmount, weeklySales)
        binding.txtGoalProgress.text = "%.0f%%".format(progress)
    }

    // ───────────────────────────────
    // 🔹 Acciones rápidas
    // ───────────────────────────────
    private fun setupQuickActions() {
        val mainActivity = requireActivity() as MainActivity

        binding.btnNewSale.setOnClickListener {
            mainActivity.navigateToInFinancesGraph(R.id.saleFormFragment)
        }

        binding.btnNewPurchase.setOnClickListener {
            mainActivity.navigateToInFinancesGraph(R.id.purchaseFormFragment)
        }

        binding.btnNewProduction.setOnClickListener {
            mainActivity.navigateToInProductsGraph(R.id.productionFormFragment)
        }

        binding.btnNewSupply.setOnClickListener {
            mainActivity.navigateToInProductsGraph(R.id.supplyFormFragment)
        }
    }

    // ───────────────────────────────
    // 🔹 Tip del día
    // ───────────────────────────────
    private fun setupDailyTip() {
        val tips = listOf(
            "Revisa productos con stock bajo para evitar rupturas.",
            "Registra tus ventas antes del cierre del día.",
            "Controla tus gastos para maximizar tu ganancia.",
            "Compara tus ventas con la semana pasada.",
            "Actualiza los precios si tus costos cambiaron."
        )

        binding.txtDailyTip.text = "Tip del día: ${tips.random()}"
    }
}