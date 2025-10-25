package com.rige.dulcegest.ui.finances.sales

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.FragmentSaleListBinding
import com.rige.dulcegest.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaleListFragment :
    BaseFragment<FragmentSaleListBinding>(FragmentSaleListBinding::inflate) {

    override val toolbarTitle = "Ventas"
    override val showToolbar = true
    override val showBackButton = true

    private val saleViewModel: SaleViewModel by viewModels()
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

        saleViewModel.sales.observe(viewLifecycleOwner) { sales ->
            adapter.submitList(sales)
        }

        binding.fabAddSale.setOnClickListener {
            findNavController().navigate(R.id.action_saleListFragment_to_saleFormFragment)
        }
    }
}