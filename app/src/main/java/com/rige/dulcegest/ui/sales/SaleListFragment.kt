package com.rige.dulcegest.ui.sales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.FragmentSaleListBinding
import com.rige.dulcegest.ui.viewmodels.SaleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaleListFragment : Fragment() {

    private var _binding: FragmentSaleListBinding? = null
    private val binding get() = _binding!!

    private val saleViewModel: SaleViewModel by viewModels()
    private lateinit var adapter: SaleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSaleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = SaleAdapter { sale ->
            Toast.makeText(requireContext(), "Venta #${sale.id}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerSales.layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerSales.adapter = adapter
        saleViewModel.sales.observe(viewLifecycleOwner) { adapter.submitList(it) }

        binding.fabAddSale.setOnClickListener {
            findNavController().navigate(R.id.action_saleListFragment_to_saleFormFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}