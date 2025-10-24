package com.rige.dulcegest.ui.supplies

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
import com.rige.dulcegest.ui.viewmodels.SupplyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupplyListFragment : Fragment() {

    private var _binding: FragmentSupplyListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SupplyViewModel by viewModels()
    private lateinit var adapter: SupplyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupplyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbarSupplyList
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        adapter = SupplyAdapter { supply ->
            findNavController().navigate(
                R.id.action_supplyListFragment_to_supplyFormFragment,
                bundleOf("supplyId" to supply.id)
            )
        }

        binding.recyclerSupplies.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSupplies.adapter = adapter

        viewModel.supplies.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        binding.fabAddSupply.setOnClickListener {
            findNavController().navigate(R.id.action_supplyListFragment_to_supplyFormFragment)
        }

        binding.fabAddPurchase.setOnClickListener {
            findNavController().navigate(R.id.action_supplyListFragment_to_purchaseFormFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}