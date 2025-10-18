package com.rige.dulcegest.ui.products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.databinding.FragmentProductionListBinding
import com.rige.dulcegest.ui.viewmodels.ProductionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductionListFragment : Fragment() {

    private var _binding: FragmentProductionListBinding? = null
    private val binding get() = _binding!!

    private val productionViewModel: ProductionViewModel by viewModels()
    private lateinit var adapter: ProductionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ProductionAdapter { batch ->
            // Aquí podrías abrir detalle del lote
        }

        binding.recyclerProductions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerProductions.adapter = adapter

        productionViewModel.batches.observe(viewLifecycleOwner) { batches ->
            adapter.submitList(batches)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
