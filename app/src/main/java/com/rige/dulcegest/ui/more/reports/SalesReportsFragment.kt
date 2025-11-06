package com.rige.dulcegest.ui.more.reports

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.databinding.FragmentSalesReportsBinding
import com.rige.dulcegest.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class SalesReportsFragment : BaseFragment<FragmentSalesReportsBinding>(
    FragmentSalesReportsBinding::inflate
) {
    override val toolbarTitle: String = "Reporte de Ventas"
    override val showBackButton: Boolean = true

    private val viewModel: SalesReportsViewModel by viewModels()

    private val dailySalesAdapter by lazy { SalesReportAdapter() }
    private val monthlyWeeklySalesAdapter by lazy { SalesReportAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerViews() {
        binding.rvDailySales.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dailySalesAdapter
        }
        binding.rvMonthlyWeeklySales.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = monthlyWeeklySalesAdapter
        }
    }

    private fun setupObservers() {
        // Observa la lista de ventas diarias
        viewModel.dailySalesSummary.observe(viewLifecycleOwner) { data ->
            dailySalesAdapter.submitList(data)
        }

        // Observa la lista de ventas semanales dentro del mes
        viewModel.monthlyWeeklySales.observe(viewLifecycleOwner) { data ->
            monthlyWeeklySalesAdapter.submitList(data)
        }

        // Observa y actualiza el mes actual en el UI
        viewModel.currentYearMonth.observe(viewLifecycleOwner) { yearMonth ->
            // Formato: "Noviembre 2025"
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))
            binding.tvCurrentMonth.text = yearMonth.format(formatter).replaceFirstChar { it.uppercase() }
        }
    }

    private fun setupListeners() {
        binding.btnPrevMonth.setOnClickListener {
            viewModel.currentYearMonth.value?.let { current ->
                viewModel.setMonth(current.minusMonths(1))
            }
        }

        binding.btnNextMonth.setOnClickListener {
            viewModel.currentYearMonth.value?.let { current ->
                viewModel.setMonth(current.plusMonths(1))
            }
        }
    }
}