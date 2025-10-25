package com.rige.dulcegest.ui.products.supplies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.FragmentSupplyListBinding
import com.rige.dulcegest.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupplyListFragment :
    BaseFragment<FragmentSupplyListBinding>(FragmentSupplyListBinding::inflate) {

    override val toolbarTitle = "Lista de Insumos"
    override val showToolbar = true
    override val showBackButton = true

    private val viewModel: SupplyViewModel by viewModels()
    private lateinit var adapter: SupplyAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SupplyAdapter { supply ->
            findNavController().navigate(
                R.id.action_supplyListFragment_to_supplyFormFragment,
                bundleOf("supplyId" to supply.id)
            )
        }

        binding.recyclerSupplies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SupplyListFragment.adapter
        }

        viewModel.supplies.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.fabAddSupply.setOnClickListener {
            findNavController().navigate(R.id.action_supplyListFragment_to_supplyFormFragment)
        }

        binding.fabAddPurchase.setOnClickListener {
            findNavController().navigate(R.id.action_supplyListFragment_to_purchaseFormFragment)
        }
    }
}