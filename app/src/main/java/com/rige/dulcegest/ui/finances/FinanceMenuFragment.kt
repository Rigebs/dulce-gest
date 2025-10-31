package com.rige.dulcegest.ui.finances

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.FragmentFinanceMenuBinding
import com.rige.dulcegest.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FinanceMenuFragment :
    BaseFragment<FragmentFinanceMenuBinding>(FragmentFinanceMenuBinding::inflate) {

    override val showToolbar: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardSales.setOnClickListener {
            findNavController().navigate(R.id.action_financeMenu_to_saleListFragment)
        }

        binding.cardPurchases.setOnClickListener {
            findNavController().navigate(R.id.action_financeMenu_to_purchaseListFragment)
        }

        binding.cardExpenses.setOnClickListener {
            findNavController().navigate(R.id.action_financeMenu_to_expenseListFragment)
        }

        binding.cardShoppingList.setOnClickListener {
            findNavController().navigate(R.id.action_financeMenu_to_shoppingListFragment)
        }
    }
}