package com.rige.dulcegest.ui.products

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.data.db.entities.Ingredient
import com.rige.dulcegest.data.db.entities.ProductRecipe
import com.rige.dulcegest.databinding.FragmentProductRecipeBinding
import com.rige.dulcegest.ui.viewmodels.IngredientViewModel
import com.rige.dulcegest.ui.viewmodels.ProductViewModel

/*
class ProductRecipeFragment : Fragment() {

    private var _binding: FragmentProductRecipeBinding? = null
    private val binding get() = _binding!!
    private val productViewModel: ProductViewModel by viewModels()
    private val ingredientViewModel: IngredientViewModel by viewModels()
    private val args: ProductRecipeFragmentArgs by navArgs()
    private lateinit var adapter: RecipeAdapter
    private var ingredientsList: List<Ingredient> = emptyList()
    private var currentRecipe: List<ProductRecipe> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val productId = args.productId

        adapter = RecipeAdapter { _, _ ->
            // El RecipeAdapter solo mantiene el mapa interno
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // 1️⃣ Observa los ingredientes
        ingredientViewModel.ingredients.observe(viewLifecycleOwner, Observer { ingredients ->
            ingredientsList = ingredients
            loadRecipe(productId)
        })

        // Botón para guardar receta
        binding.btnSave.setOnClickListener {
            val recipeToSave = adapter.getCurrentRecipe().map { it.first.copy(productId = productId) }
            productViewModel.setRecipe(productId, recipeToSave)
            Toast.makeText(requireContext(), "Receta guardada ✅", Toast.LENGTH_SHORT).show()
            updateTotalCost()
        }
    }

    private fun loadRecipe(productId: Long) {
        productViewModel.getRecipe(productId).observe(viewLifecycleOwner) { recipe ->
            currentRecipe = recipe
            adapter.submitData(ingredientsList, recipe)
            updateTotalCost()
        }
    }

    private fun updateTotalCost() {
        val total = adapter.getCurrentRecipe().sumOf { (recipe, ingredientCost) ->
            recipe.qtyPerUnit * ingredientCost
        }
        binding.textTotalCost.text = "Costo por unidad: ₡%.2f".format(total)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}*/
