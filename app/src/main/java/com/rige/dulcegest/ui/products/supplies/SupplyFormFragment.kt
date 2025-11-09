package com.rige.dulcegest.ui.products.supplies

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.FragmentSupplyFormBinding
import com.rige.dulcegest.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupplyFormFragment :
    BaseFragment<FragmentSupplyFormBinding>(FragmentSupplyFormBinding::inflate) {

    override val toolbarTitle: String
        get() = if (supplyId == 0L) "Registrar Insumo" else "Editar Insumo"

    override val showToolbar = true
    override val showBackButton = true

    private val viewModel: SupplyViewModel by viewModels()
    private var supplyId: Long = 0L
    private lateinit var unitsAdapter: ArrayAdapter<CharSequence>
    private lateinit var purchaseUnitsAdapter: ArrayAdapter<CharSequence>

    private val purchaseUnitHint = "Sin unidad de compra"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        unitsAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.units_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        }
        binding.spinnerUnit.adapter = unitsAdapter

        val purchaseUnitsStrings = resources.getStringArray(R.array.purchase_units_array).toMutableList()

        if (purchaseUnitsStrings.firstOrNull()?.isEmpty() == true) {
            purchaseUnitsStrings.removeAt(0)
        }

        purchaseUnitsStrings.add(0, purchaseUnitHint)

        val purchaseUnitsCharSequence: List<CharSequence> = purchaseUnitsStrings.map { it as CharSequence }

        purchaseUnitsAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            purchaseUnitsCharSequence
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        }
        binding.spinnerPurchaseUnit.adapter = purchaseUnitsAdapter

        supplyId = arguments?.getLong("supplyId") ?: 0L

        if (supplyId != 0L) {
            loadExistingSupply(supplyId)
        } else {
            binding.spinnerPurchaseUnit.setSelection(0)
        }

        binding.btnSave.setOnClickListener { saveSupply() }
    }

    private fun loadExistingSupply(id: Long) {
        viewModel.getSupplyById(id).observe(viewLifecycleOwner) { supply ->
            supply?.let {
                binding.inputName.setText(it.name)

                val unitPosition = unitsAdapter.getPosition(it.unit)
                if (unitPosition >= 0) binding.spinnerUnit.setSelection(unitPosition)

                it.purchaseUnit?.let { pu ->
                    val purchaseUnitPosition = purchaseUnitsAdapter.getPosition(pu)
                    if (purchaseUnitPosition >= 0) {
                        binding.spinnerPurchaseUnit.setSelection(purchaseUnitPosition)
                    } else {
                        binding.spinnerPurchaseUnit.setSelection(0)
                    }
                } ?: binding.spinnerPurchaseUnit.setSelection(0)

                binding.inputStock.setText(it.stockQty.toString())
                binding.inputConversionFactor.setText(it.conversionFactor?.toString() ?: "")
                binding.inputNotes.setText(it.notes ?: "")
            }
        }
    }

    private fun saveSupply() {
        val name = binding.inputName.text.toString().trim()
        val unit = binding.spinnerUnit.selectedItem.toString()

        val selectedPurchaseUnit = binding.spinnerPurchaseUnit.selectedItem.toString()
        val purchaseUnit = if (selectedPurchaseUnit == purchaseUnitHint) {
            // Si el texto es el hint, guardamos NULL
            null
        } else {
            selectedPurchaseUnit.trim().ifEmpty { null }
        }

        val stock = binding.inputStock.text.toString().toDoubleOrNull() ?: 0.0
        val notes = binding.inputNotes.text.toString().trim().ifEmpty { null }
        val conversionFactor = binding.inputConversionFactor.text.toString().toDoubleOrNull()

        if (name.isEmpty() || unit.isEmpty()) {
            Toast.makeText(requireContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.saveSupply(
            supplyId,
            name,
            unit,
            stock,
            purchaseUnit, // Usamos la variable opcional
            conversionFactor,
            notes
        ).invokeOnCompletion { throwable ->
            if (throwable == null) {
                val message = if (supplyId == 0L) "Insumo agregado" else "Insumo actualizado"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Error al guardar el insumo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}