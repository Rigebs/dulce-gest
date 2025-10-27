package com.rige.dulcegest.ui.finances.purchases

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rige.dulcegest.data.local.entities.Purchase
import com.rige.dulcegest.data.local.entities.Supply
import com.rige.dulcegest.databinding.FragmentPurchaseFormBinding
import com.rige.dulcegest.ui.common.BaseFragment
import com.rige.dulcegest.ui.products.supplies.SupplyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

@AndroidEntryPoint
class PurchaseFormFragment :
    BaseFragment<FragmentPurchaseFormBinding>(FragmentPurchaseFormBinding::inflate) {

    override val toolbarTitle = "Registrar compra"
    override val showToolbar = true
    override val showBackButton = true

    private val viewModel: PurchaseViewModel by viewModels()
    private val supplyViewModel: SupplyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            val date = LocalDateTime.now().toString()

            if (selectedSupply == null || quantity <= 0.0 || totalPrice <= 0.0) {
                Toast.makeText(requireContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // Guardar la compra
                val purchase = Purchase(
                    supplyId = selectedSupply.id,
                    quantity = quantity,
                    totalPrice = totalPrice,
                    supplier = supplier,
                    date = date,
                    notes = notes
                )
                viewModel.insert(purchase)

                // 🔹 Convertir cantidad de compra a unidades base
                val factor = selectedSupply.conversionFactor ?: 1.0
                val addedQty = quantity * factor // cantidad en unidades base
                val newStock = selectedSupply.stockQty + addedQty

                // 🔹 Calcular costo por unidad base
                val newUnitCost = totalPrice / addedQty

                // 🔹 Actualizar costo promedio
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
}
