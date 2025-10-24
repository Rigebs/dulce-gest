package com.rige.dulcegest.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.FragmentHomeBinding
import com.rige.dulcegest.ui.MainActivity
import com.rige.dulcegest.ui.viewmodels.ExpenseViewModel
import com.rige.dulcegest.ui.viewmodels.PurchaseViewModel
import com.rige.dulcegest.ui.viewmodels.SaleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val saleViewModel: SaleViewModel by viewModels()
    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val purchaseViewModel: PurchaseViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupObservers()
        setupQuickActions()
        setupDailyTip()
    }

    private fun setupObservers() {

        // Datos semanales para las nuevas cards
        saleViewModel.getTotalSalesThisWeek().observe(viewLifecycleOwner) { weeklySales ->
            binding.txtWeeklySales.text = "S/ %.2f".format(weeklySales ?: 0.0)
            updateWeeklyProfit()
            updateWeeklyGoal(weeklySales ?: 0.0)
        }

        expenseViewModel.getTotalExpensesThisWeek().observe(viewLifecycleOwner) { weeklyExpenses ->
            binding.txtWeeklyExpenses.text = "S/ %.2f".format(weeklyExpenses ?: 0.0)
            updateWeeklyProfit()
        }

        // Recent Sales
        homeViewModel.recentSales.observe(viewLifecycleOwner) { sales ->
            binding.rvRecentSales.apply {
                adapter = RecentSalesAdapter(sales)
                layoutManager = LinearLayoutManager(requireContext())
            }
        }

        homeViewModel.lowStockSupplies.observe(viewLifecycleOwner) { lowStock ->
            if (lowStock.isEmpty()) {
                binding.cardLowStock.visibility = View.GONE
            } else {
                binding.cardLowStock.visibility = View.VISIBLE
                binding.txtLowStock.text = "Insumos críticos: " +
                        lowStock.joinToString(", ") { it.name }
            }
        }

        purchaseViewModel.getTotalPurchasesThisWeek().observe(viewLifecycleOwner) { weeklyPurchases ->
            binding.txtWeeklyPurchases.text = "S/ %.2f".format(weeklyPurchases ?: 0.0)
            updateWeeklyProfit()
        }
    }

    // Ganancia semanal
    private fun updateWeeklyProfit() {
        val weeklySales = binding.txtWeeklySales.text.toString().replace("S/", "").trim().toDoubleOrNull() ?: 0.0
        val weeklyPurchases = binding.txtWeeklyPurchases.text.toString().replace("S/", "").trim().toDoubleOrNull() ?: 0.0
        val weeklyExpenses = binding.txtWeeklyExpenses.text.toString().replace("S/", "").trim().toDoubleOrNull() ?: 0.0

        val weeklyProfit = weeklySales - (weeklyPurchases + weeklyExpenses)
        binding.txtWeeklyProfit.text = "S/ %.2f".format(weeklyProfit)
    }

    private fun updateWeeklyGoal(weeklySales: Double) {
        val goalAmount = 100.0
        val progress = ((weeklySales / goalAmount) * 100).coerceAtMost(100.0)
        binding.progressWeeklyGoal.progress = progress.toInt()
        binding.txtGoalSummary.text = "Objetivo: S/ %.2f — Alcanzado: S/ %.2f".format(goalAmount, weeklySales)
        binding.txtGoalProgress.text = "%.0f%%".format(progress)
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
