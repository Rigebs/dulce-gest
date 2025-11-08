package com.rige.dulcegest.ui.products.list

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.FragmentProductListBinding
import com.rige.dulcegest.ui.common.BaseFragment
import com.rige.dulcegest.ui.products.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListFragment :
    BaseFragment<FragmentProductListBinding>(FragmentProductListBinding::inflate),
    BaseFragment.SearchableFragment {

    override val toolbarTitle = "Productos"
    override val showToolbar = true
    override val showSearchView = true
    override val showBackButton = true

    private val productViewModel: ProductViewModel by activityViewModels()
    private lateinit var adapter: ProductAdapter

    private var currentSearchQuery: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductAdapter { product ->
            val bundle = Bundle().apply { putLong("productId", product.id) }
            findNavController().navigate(R.id.action_productListFragment_to_productFormFragment, bundle)
        }

        binding.recyclerProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ProductListFragment.adapter
        }

        productViewModel.filteredProducts.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
            updateEmptyState(products.isEmpty(), currentSearchQuery)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        productViewModel.filterProducts(null)

        binding.fabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_productListFragment_to_productFormFragment)
        }

        binding.btnClearSearch.setOnClickListener {
            productViewModel.filterProducts(null)
            currentSearchQuery = null
            clearSearchViewText()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            productViewModel.filterProducts(currentSearchQuery)
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
        productViewModel.filterProducts(newText)
    }

    override fun onQueryTextSubmit(query: String?) {}
}