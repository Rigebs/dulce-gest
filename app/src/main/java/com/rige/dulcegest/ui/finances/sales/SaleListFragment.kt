package com.rige.dulcegest.ui.finances.sales

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.R
import com.rige.dulcegest.core.utils.DateUtils
import com.rige.dulcegest.core.utils.toSoles
import com.rige.dulcegest.databinding.FragmentSaleListBinding
import com.rige.dulcegest.domain.enums.DateRangeFilter
import com.rige.dulcegest.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaleListFragment :
    BaseFragment<FragmentSaleListBinding>(FragmentSaleListBinding::inflate) {

    override val toolbarTitle = "Ventas"
    override val showToolbar = true
    override val showBackButton = true

    private val saleViewModel: SaleViewModel by activityViewModels()
    private lateinit var adapter: SaleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SaleAdapter { sale ->
            Toast.makeText(requireContext(), "Venta #${sale.id}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerSales.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SaleListFragment.adapter
        }

        saleViewModel.filteredSales.observe(viewLifecycleOwner) { sales ->
            println(sales.size)
            adapter.submitList(sales)
            updateEmptyState(sales.isEmpty(), saleViewModel.filterState.value?.selectedRange)
        }

        saleViewModel.totalAmount.observe(viewLifecycleOwner) { total ->
            binding.txtSaleTotal.text = "Total: ${total.toSoles()}"
        }

        saleViewModel.filterState.observe(viewLifecycleOwner) { state ->
            binding.txtSalePeriod.text = DateUtils.getPeriodTitle(
                state.selectedRange,
                state.startDate,
                state.endDate
            )
            saleViewModel.filteredSales.value?.let { sales ->
                updateEmptyState(sales.isEmpty(), state.selectedRange)
            }
        }

        binding.fabAddSale.setOnClickListener {
            findNavController().navigate(R.id.action_saleListFragment_to_saleFormFragment)
        }

        binding.fabFilterSales.setOnClickListener {
            val dialog = SaleFilterDialogFragment()
            dialog.show(parentFragmentManager, "SaleFilterDialog")
        }

        binding.btnClearFilter.setOnClickListener {
            saleViewModel.resetFilters()
        }
    }

    private fun updateEmptyState(isEmpty: Boolean, selectedRange: DateRangeFilter?) {
        val isFiltered = selectedRange != DateRangeFilter.CURRENT_MONTH

        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerSales.visibility = if (isEmpty) View.GONE else View.VISIBLE

        if (isEmpty) {
            if (isFiltered) {
                binding.emptyStateTitle.text = getString(R.string.empty_filter_range_title)
                binding.emptyStateMessage.text = getString(R.string.empty_filter_range_message)
                binding.btnClearFilter.visibility = View.VISIBLE
            }
            else {
                binding.emptyStateTitle.text = getString(R.string.empty_list_title)
                binding.emptyStateMessage.text = getString(R.string.empty_list_message)
                binding.btnClearFilter.visibility = View.GONE
            }
        }
    }
}