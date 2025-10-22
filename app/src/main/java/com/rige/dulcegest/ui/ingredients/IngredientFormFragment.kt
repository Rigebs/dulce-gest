package com.rige.dulcegest.ui.ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rige.dulcegest.R
import com.rige.dulcegest.data.db.entities.Ingredient
import com.rige.dulcegest.databinding.FragmentIngredientFormBinding
import com.rige.dulcegest.ui.viewmodels.IngredientViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

@AndroidEntryPoint
class IngredientFormFragment : Fragment(R.layout.fragment_ingredient_form) {

    private var _binding: FragmentIngredientFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IngredientViewModel by viewModels()
    private var ingredientId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbarIngredientForm
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        // Configurar Spinner de unidades
        val unitsAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.units_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        }
        binding.spinnerUnit.adapter = unitsAdapter

        ingredientId = arguments?.getLong("ingredientId")

        toolbar.title = if (ingredientId == null || ingredientId == 0L)
            "Crear ingrediente" else "Editar ingrediente"

        ingredientId?.let { id ->
            viewModel.getIngredientById(id).observe(viewLifecycleOwner) { ingredient ->
                ingredient?.let {
                    binding.inputName.setText(it.name)
                    val unitPosition = unitsAdapter.getPosition(it.unit)
                    if (unitPosition >= 0) binding.spinnerUnit.setSelection(unitPosition)
                    binding.inputStock.setText(it.stockQty.toString())
                    binding.inputPurchaseUnit.setText(it.purchaseUnit ?: "")
                    binding.inputConversionFactor.setText(it.conversionFactor?.toString() ?: "")
                    binding.inputNotes.setText(it.notes ?: "")
                }
            }
        }

        binding.btnSave.setOnClickListener { saveIngredient() }
    }

    private fun saveIngredient() {
        val name = binding.inputName.text.toString().trim()
        val unit = binding.spinnerUnit.selectedItem.toString()
        val stock = binding.inputStock.text.toString().toDoubleOrNull() ?: 0.0
        val notes = binding.inputNotes.text.toString().trim().ifEmpty { null }
        val purchaseUnit = binding.inputPurchaseUnit.text.toString().trim().ifEmpty { null }
        val conversionFactor = binding.inputConversionFactor.text.toString().toDoubleOrNull()

        if (name.isEmpty() || unit.isEmpty()) {
            Toast.makeText(requireContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val ingredient = Ingredient(
            id = ingredientId ?: 0L,
            name = name,
            unit = unit,
            stockQty = stock,
            purchaseUnit = purchaseUnit,
            conversionFactor = conversionFactor,
            updatedAt = LocalDateTime.now().toString(),
            notes = notes
        )

        lifecycleScope.launch {
            if (ingredientId == null || ingredientId == 0L) {
                viewModel.insert(ingredient)
                Toast.makeText(requireContext(), "Ingrediente agregado", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.update(ingredient)
                Toast.makeText(requireContext(), "Ingrediente actualizado", Toast.LENGTH_SHORT).show()
            }
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}