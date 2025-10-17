package com.rige.dulcegest.ui.ingredients

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rige.dulcegest.R
import com.rige.dulcegest.data.db.entities.Ingredient
import com.rige.dulcegest.databinding.FragmentIngredientFormBinding
import com.rige.dulcegest.ui.viewmodels.IngredientViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDateTime

@AndroidEntryPoint
class IngredientFormFragment : Fragment(R.layout.fragment_ingredient_form) {

    private var _binding: FragmentIngredientFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IngredientViewModel by viewModels()
    private var ingredientId: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentIngredientFormBinding.bind(view)

        // Recuperar argumento (si viene desde la lista)
        ingredientId = arguments?.getLong("ingredientId")

        // Si hay un ID, cargar datos existentes (modo edición)
        ingredientId?.let { id ->
            viewModel.getIngredientById(id).observe(viewLifecycleOwner) { ingredient ->
                ingredient?.let {
                    binding.inputName.setText(it.name)
                    binding.inputUnit.setText(it.unit)
                    binding.inputCost.setText(it.costPerUnit.toString())
                    binding.inputStock.setText(it.stockQty.toString())
                    binding.inputNotes.setText(it.notes ?: "")
                }
            }
        }

        binding.btnSave.setOnClickListener {
            saveIngredient()
        }
    }

    private fun saveIngredient() {
        val name = binding.inputName.text.toString().trim()
        val unit = binding.inputUnit.text.toString().trim()
        val cost = binding.inputCost.text.toString().toDoubleOrNull() ?: 0.0
        val stock = binding.inputStock.text.toString().toDoubleOrNull() ?: 0.0
        val notes = binding.inputNotes.text.toString().trim().ifEmpty { null }

        if (name.isEmpty() || unit.isEmpty()) {
            Toast.makeText(requireContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val ingredient = Ingredient(
            id = ingredientId ?: 0,
            name = name,
            unit = unit,
            costPerUnit = cost,
            stockQty = stock,
            updatedAt = LocalDateTime.now().toString(),
            notes = notes
        )

        if (ingredientId == null) {
            viewModel.insert(ingredient)
            Toast.makeText(requireContext(), "Ingrediente agregado", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.update(ingredient)
            Toast.makeText(requireContext(), "Ingrediente actualizado", Toast.LENGTH_SHORT).show()
        }

        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}