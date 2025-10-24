package com.rige.dulcegest.ui.products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.FragmentProductMenuBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductMenuFragment : Fragment(R.layout.fragment_product_menu) {

    private var _binding: FragmentProductMenuBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProductMenuBinding.bind(view)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}