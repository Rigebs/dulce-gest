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
import com.rige.dulcegest.data.db.entities.Supply
import com.rige.dulcegest.data.db.entities.Purchase
import com.rige.dulcegest.databinding.FragmentPurchaseFormBinding
import com.rige.dulcegest.ui.viewmodels.SupplyViewModel
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
    private val supplyViewModel: SupplyViewModel by viewModels()

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

        setupSupplySpinner()
        setupSaveButton()
    }

    private fun setupSupplySpinner() {
        supplyViewModel.supplies.observe(viewLifecycleOwner) { supplies ->
            val adapter = SupplySpinnerAdapter(requireContext(), supplies)
            binding.spinnerSupply.adapter = adapter
        }
    }

    private fun setupSaveButton() {
        binding.btnSavePurchase.setOnClickListener {
            val selectedSupply = binding.spinnerSupply.selectedItem as? Supply
            val quantity = binding.inputQuantity.text.toString().toDoubleOrNull() ?: 0.0
            val totalPrice = binding.inputTotalPrice.text.toString().toDoubleOrNull() ?: 0.0
            val supplier = binding.inputSupplier.text.toString().trim().ifEmpty { null }
            val notes = binding.inputNotes.text.toString().trim().ifEmpty { null }
            val date = LocalDate.now().toString()

            if (selectedSupply == null || quantity <= 0.0 || totalPrice <= 0.0) {
                Toast.makeText(requireContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val purchase = Purchase(
                    supplyId = selectedSupply.id,
                    quantity = quantity,
                    totalPrice = totalPrice,
                    supplier = supplier,
                    date = date,
                    notes = notes
                )

                // 1️⃣ Guardar la compra
                viewModel.insert(purchase)

                // 2️⃣ Calcular nueva cantidad en stock
                val factor = selectedSupply.conversionFactor ?: 1.0
                val addedQty = quantity * factor
                val newStock = selectedSupply.stockQty + addedQty

                // 3️⃣ Calcular nuevo costo promedio
                val newUnitCost = totalPrice / quantity
                val oldStock = selectedSupply.stockQty
                val oldCost = selectedSupply.avgCost

                val newAvgCost = if (oldStock + addedQty > 0) {
                    ((oldStock * oldCost) + (addedQty * newUnitCost)) / (oldStock + addedQty)
                } else {
                    newUnitCost
                }

                val updatedSupply = selectedSupply.copy(
                    stockQty = newStock,
                    avgCost = newAvgCost,
                    updatedAt = LocalDateTime.now().toString()
                )

                supplyViewModel.update(updatedSupply)

                Toast.makeText(
                    requireContext(),
                    "Compra registrada: +${addedQty} ${selectedSupply.unit} añadidas al stock\nNuevo costo promedio: ${"%.2f".format(newAvgCost)}",
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
