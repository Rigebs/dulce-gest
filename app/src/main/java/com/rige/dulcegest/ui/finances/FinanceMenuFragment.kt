package com.rige.dulcegest.ui.finances

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.FragmentFinanceMenuBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FinanceMenuFragment : Fragment(R.layout.fragment_finance_menu) {

    private var _binding: FragmentFinanceMenuBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFinanceMenuBinding.bind(view)

        // Card: Ventas
        binding.cardSales.setOnClickListener {
            findNavController().navigate(R.id.action_financeMenu_to_saleListFragment)
        }

        // Card: Compras
        binding.cardPurchases.setOnClickListener {
            findNavController().navigate(R.id.action_financeMenu_to_purchaseFormFragment)
        }

        // Card: Gastos
        binding.cardExpenses.setOnClickListener {
            findNavController().navigate(R.id.action_financeMenu_to_expenseListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
