package com.rige.dulcegest.ui.products

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.FragmentProductMenuBinding
import com.rige.dulcegest.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductMenuFragment :
    BaseFragment<FragmentProductMenuBinding>(FragmentProductMenuBinding::inflate) {

    override val showToolbar: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardProducts.setOnClickListener {
            findNavController().navigate(R.id.action_productMenu_to_productListFragment)
        }

        binding.cardSupplies.setOnClickListener {
            findNavController().navigate(R.id.action_productMenu_to_supplyListFragment)
        }

        binding.cardProduction.setOnClickListener {
            findNavController().navigate(R.id.action_productMenu_to_productionListFragment)
        }
    }
}