package com.rige.dulcegest.ui.ingredients

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
import com.rige.dulcegest.databinding.FragmentIngredientListBinding
import com.rige.dulcegest.ui.viewmodels.IngredientViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IngredientListFragment : Fragment() {

    private var _binding: FragmentIngredientListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IngredientViewModel by viewModels()
    private lateinit var adapter: IngredientAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = IngredientAdapter { ingredient ->
            findNavController().navigate(
                R.id.action_ingredientListFragment_to_ingredientFormFragment,
                bundleOf("ingredientId" to ingredient.id)
            )
        }

        binding.recyclerIngredients.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerIngredients.adapter = adapter

        viewModel.ingredients.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        binding.fabAddIngredient.setOnClickListener {
            findNavController().navigate(R.id.action_ingredientListFragment_to_ingredientFormFragment)
        }

        binding.fabAddPurchase.setOnClickListener {
            findNavController().navigate(R.id.action_ingredientListFragment_to_purchaseFormFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}