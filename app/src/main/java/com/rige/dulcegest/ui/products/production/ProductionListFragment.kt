package com.rige.dulcegest.ui.products.production

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.FragmentProductionListBinding
import com.rige.dulcegest.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductionListFragment :
    BaseFragment<FragmentProductionListBinding>(FragmentProductionListBinding::inflate) {

    override val toolbarTitle = "Lista de Producciones"
    override val showToolbar = true
    override val showBackButton = true

    private val viewModel: ProductionViewModel by viewModels()
    private lateinit var adapter: ProductionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductionAdapter { batch ->
        }

        binding.recyclerProductions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ProductionListFragment.adapter
        }

        viewModel.batches.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.fabAddProduction.setOnClickListener {
            findNavController().navigate(R.id.action_productionListFragment_to_productionFormFragment)
        }
    }
}
