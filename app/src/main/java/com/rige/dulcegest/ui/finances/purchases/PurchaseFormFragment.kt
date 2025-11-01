package com.rige.dulcegest.ui.finances.purchases

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rige.dulcegest.data.local.entities.Supply
import com.rige.dulcegest.databinding.FragmentPurchaseFormBinding
import com.rige.dulcegest.ui.common.BaseFragment
import com.rige.dulcegest.ui.products.supplies.SupplyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

        val preselectedId = arguments?.getLong("supplyId") ?: 0

        supplyViewModel.supplies.observe(viewLifecycleOwner) { supplies ->
            val adapter = SupplySpinnerAdapter(requireContext(), supplies)
            binding.spinnerSupply.adapter = adapter

            if (preselectedId != -1L) {
                val index = supplies.indexOfFirst { it.id == preselectedId }
                if (index >= 0) binding.spinnerSupply.setSelection(index)
            }
        }

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

            if (selectedSupply == null || quantity <= 0.0 || totalPrice <= 0.0) {
                Toast.makeText(requireContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🟢 El ViewModel y el Use Case manejan la transacción
            lifecycleScope.launch {
                try {
                    viewModel.registerPurchase(
                        selectedSupply,
                        quantity,
                        totalPrice,
                        supplier,
                        notes
                    ).join()

                    Toast.makeText(
                        requireContext(),
                        "Compra registrada y stock actualizado.",
                        Toast.LENGTH_LONG
                    ).show()

                    findNavController().navigateUp()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error al registrar la compra.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}