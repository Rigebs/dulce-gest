package com.rige.dulcegest.ui.purchases

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rige.dulcegest.R
import com.rige.dulcegest.data.db.entities.Ingredient
import com.rige.dulcegest.data.db.entities.Purchase
import com.rige.dulcegest.databinding.FragmentPurchaseFormBinding
import com.rige.dulcegest.ui.viewmodels.IngredientViewModel
import com.rige.dulcegest.ui.viewmodels.PurchaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

@AndroidEntryPoint
class PurchaseFormFragment : Fragment(R.layout.fragment_purchase_form) {

    private var _binding: FragmentPurchaseFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PurchaseViewModel by viewModels()
    private val ingredientViewModel: IngredientViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchaseFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbarPurchaseForm
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        setupIngredientSpinner()
        setupSaveButton()
    }

    private fun setupIngredientSpinner() {
        ingredientViewModel.ingredients.observe(viewLifecycleOwner) { ingredients ->
            val adapter = IngredientSpinnerAdapter(requireContext(), ingredients)
            binding.spinnerIngredient.adapter = adapter
        }
    }

    private fun setupSaveButton() {
        binding.btnSavePurchase.setOnClickListener {
            val selectedIngredient = binding.spinnerIngredient.selectedItem as? Ingredient
            val quantity = binding.inputQuantity.text.toString().toDoubleOrNull() ?: 0.0
            val totalPrice = binding.inputTotalPrice.text.toString().toDoubleOrNull() ?: 0.0
            val supplier = binding.inputSupplier.text.toString().trim().ifEmpty { null }
            val notes = binding.inputNotes.text.toString().trim().ifEmpty { null }
            val date = LocalDate.now().toString()

            if (selectedIngredient == null || quantity <= 0.0 || totalPrice <= 0.0) {
                Toast.makeText(requireContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val purchase = Purchase(
                    ingredientId = selectedIngredient.id,
                    quantity = quantity,
                    totalPrice = totalPrice,
                    supplier = supplier,
                    date = date,
                    notes = notes
                )

                // 1️⃣ Guardar la compra
                viewModel.insert(purchase)

                // 2️⃣ Calcular nueva cantidad en stock
                val factor = selectedIngredient.conversionFactor ?: 1.0
                val addedQty = quantity * factor
                val newStock = selectedIngredient.stockQty + addedQty

                // 3️⃣ Calcular nuevo costo promedio
                val newUnitCost = totalPrice / quantity
                val oldStock = selectedIngredient.stockQty
                val oldCost = selectedIngredient.avgCost

                val newAvgCost = if (oldStock + addedQty > 0) {
                    ((oldStock * oldCost) + (addedQty * newUnitCost)) / (oldStock + addedQty)
                } else {
                    newUnitCost
                }

                val updatedIngredient = selectedIngredient.copy(
                    stockQty = newStock,
                    avgCost = newAvgCost,
                    updatedAt = LocalDateTime.now().toString()
                )

                ingredientViewModel.update(updatedIngredient)

                Toast.makeText(
                    requireContext(),
                    "Compra registrada: +${addedQty} ${selectedIngredient.unit} añadidas al stock\nNuevo costo promedio: ${"%.2f".format(newAvgCost)}",
                    Toast.LENGTH_LONG
                ).show()

                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
