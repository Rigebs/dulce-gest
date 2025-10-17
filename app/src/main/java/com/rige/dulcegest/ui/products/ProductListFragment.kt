package com.rige.dulcegest.ui.products

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.FragmentProductListBinding
import com.rige.dulcegest.ui.viewmodels.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    private val productViewModel: ProductViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ProductAdapter(
            onClick = { product ->
                val bundle = Bundle().apply { putLong("productId", product.id) }
                findNavController().navigate(R.id.action_productListFragment_to_productFormFragment, bundle)
            }
        )

        binding.recyclerProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerProducts.adapter = adapter

        productViewModel.products.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.fabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_productListFragment_to_productFormFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}