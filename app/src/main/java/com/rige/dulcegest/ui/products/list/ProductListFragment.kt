package com.rige.dulcegest.ui.products.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.FragmentProductListBinding
import com.rige.dulcegest.core.utils.toPx
import com.rige.dulcegest.ui.common.BaseFragment
import com.rige.dulcegest.ui.products.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListFragment :
    BaseFragment<FragmentProductListBinding>(FragmentProductListBinding::inflate) {

    override val toolbarTitle = "Lista de Productos"
    override val showToolbar = true
    override val showBackButton = true

    private val productViewModel: ProductViewModel by activityViewModels()
    private lateinit var adapter: ProductAdapter
    private var isFabMenuOpen = false

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

        productViewModel.products.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        setupFabMenu()
    }

    private fun setupFabMenu() {
        binding.fabMain.setOnClickListener { toggleFabMenu() }
        binding.fabAddProduct.setOnClickListener {
            closeFabMenu()
            findNavController().navigate(R.id.action_productListFragment_to_productFormFragment)
        }
        binding.fabAddProduction.setOnClickListener {
            findNavController().navigate(R.id.action_productListFragment_to_productionFormFragment)
        }
        binding.fabViewProduction.setOnClickListener {
            closeFabMenu()
            findNavController().navigate(R.id.action_productListFragment_to_productionListFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        resetFabMenu()
    }

    private fun toggleFabMenu() {
        if (isFabMenuOpen) {
            closeFabMenu()
        } else {
            openFabMenu()
        }
    }

    private fun openFabMenu() {
        isFabMenuOpen = true

        binding.fabAddProduct.apply {
            visibility = View.VISIBLE
            animate().translationY((-90f).toPx(requireContext())).alpha(1f).setDuration(200).start()
        }

        binding.fabAddProduction.apply {
            visibility = View.VISIBLE
            animate().translationY((-160f).toPx(requireContext())).alpha(1f).setDuration(200).start()
        }

        binding.fabViewProduction.apply {
            visibility = View.VISIBLE
            animate().translationY((-230f).toPx(requireContext())).alpha(1f).setDuration(200).start()
        }

        binding.fabMain.animate().rotation(90f).setDuration(200).start()
    }

    private fun closeFabMenu() {
        isFabMenuOpen = false

        binding.fabAddProduct.animate().translationY(0f).alpha(0f).setDuration(200)
            .withEndAction { binding.fabAddProduct.visibility = View.GONE }.start()

        binding.fabAddProduction.animate().translationY(0f).alpha(0f).setDuration(200)
            .withEndAction { binding.fabAddProduction.visibility = View.GONE }.start()

        binding.fabViewProduction.animate().translationY(0f).alpha(0f).setDuration(200)
            .withEndAction { binding.fabViewProduction.visibility = View.GONE }.start()

        binding.fabMain.animate().rotation(0f).setDuration(200).start()
    }

    private fun resetFabMenu() {
        isFabMenuOpen = false

        binding.fabAddProduct.apply {
            visibility = View.GONE
            translationY = 0f
            alpha = 0f
        }

        binding.fabAddProduction.apply {
            visibility = View.GONE
            translationY = 0f
            alpha = 0f
        }

        binding.fabViewProduction.apply {
            visibility = View.GONE
            translationY = 0f
            alpha = 0f
        }

        binding.fabMain.rotation = 0f
    }

    override fun onDestroyView() {
        binding.fabAddProduct.animate().cancel()
        binding.fabAddProduction.animate().cancel()
        binding.fabViewProduction.animate().cancel()
        binding.fabMain.animate().cancel()

        super.onDestroyView()
    }
}