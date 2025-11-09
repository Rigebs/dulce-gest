package com.rige.dulcegest.ui.products.supplies

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.FragmentSupplyListBinding
import com.rige.dulcegest.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupplyListFragment :
    BaseFragment<FragmentSupplyListBinding>(FragmentSupplyListBinding::inflate),
    BaseFragment.SearchableFragment {

    override val toolbarTitle = "Insumos"
    override val showToolbar = true
    override val showSearchView = true
    override val showBackButton = true

    private val viewModel: SupplyViewModel by activityViewModels()
    private lateinit var adapter: SupplyAdapter

    private var currentSearchQuery: String? = null

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

        viewModel.filteredSupplies.observe(viewLifecycleOwner) { supplies ->
            adapter.submitList(supplies)
            updateEmptyState(supplies.isEmpty(), currentSearchQuery)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.filterSupplies(null)

        binding.fabAddSupply.setOnClickListener {
            findNavController().navigate(R.id.action_supplyListFragment_to_supplyFormFragment)
        }

        binding.btnClearSearch.setOnClickListener {
            viewModel.filterSupplies(null)
            currentSearchQuery = null
            clearSearchViewText()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.filterSupplies(currentSearchQuery)
        }
    }

    private fun updateEmptyState(isEmpty: Boolean, searchQuery: String?) {
        binding.emptyStateContainer.isVisible = isEmpty
        binding.swipeRefreshLayout.isVisible = !isEmpty

        if (isEmpty) {
            if (!searchQuery.isNullOrBlank()) {
                binding.emptyStateTitle.text = getString(R.string.empty_search_title)
                binding.emptyStateMessage.text = getString(R.string.empty_search_message)
                binding.btnClearSearch.isVisible = true
            }
            else {
                binding.emptyStateTitle.text = getString(R.string.empty_list_title)
                binding.emptyStateMessage.text = getString(R.string.empty_list_message)
                binding.btnClearSearch.isVisible = false
            }
        }
    }

    override fun onQueryTextChange(newText: String?) {
        currentSearchQuery = newText
        viewModel.filterSupplies(newText)
    }

    override fun onQueryTextSubmit(query: String?) {}
}