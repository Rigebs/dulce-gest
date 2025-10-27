package com.rige.dulcegest.ui.products.productions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rige.dulcegest.R
import com.rige.dulcegest.data.local.entities.relations.ProductionBatchWithProductAndConsumptions
import com.rige.dulcegest.databinding.FragmentProductionListBinding
import com.rige.dulcegest.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductionListFragment :
    BaseFragment<FragmentProductionListBinding>(FragmentProductionListBinding::inflate) {

    override val toolbarTitle = "Producciones"
    override val showToolbar = true
    override val showBackButton = true

    private val viewModel: ProductionViewModel by viewModels()
    private lateinit var adapter: ProductionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductionAdapter(
            onEdit = { batch ->
                val action = ProductionListFragmentDirections
                    .actionProductionListFragmentToProductionFormFragment(batch.batch.id)
                findNavController().navigate(action)
            },
            onDelete = { batch -> deleteWithUndo(batch) }
        )

        binding.recyclerProductions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerProductions.adapter = adapter

        viewModel.batchesWithProductAndConsumptions.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.emptyStateLayout.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAddProduction.setOnClickListener {
            findNavController().navigate(R.id.action_productionListFragment_to_productionFormFragment)
        }
    }

    private fun deleteWithUndo(batch: ProductionBatchWithProductAndConsumptions) {
        val deletedBatch = batch.batch
        val deletedConsumptions = batch.consumptions

        viewModel.deleteBatch(deletedBatch)

        Snackbar.make(requireView(), "Producción eliminada", Snackbar.LENGTH_LONG)
            .setAction("DESHACER") {
                lifecycleScope.launch {
                    viewModel.insertBatch(deletedBatch, deletedConsumptions)
                }
            }
            .show()
    }
}