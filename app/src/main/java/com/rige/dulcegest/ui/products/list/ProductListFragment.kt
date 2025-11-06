package com.rige.dulcegest.ui.products.list

import android.os.Bundle
import android.view.View
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

        productViewModel.filteredProducts.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        productViewModel.filterProducts(null)

        binding.fabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_productListFragment_to_productFormFragment)
        }
    }

    override fun onQueryTextChange(newText: String?) {
        productViewModel.filterProducts(newText)
    }

    override fun onQueryTextSubmit(query: String?) {}
}