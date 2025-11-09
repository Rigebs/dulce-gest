package com.rige.dulcegest.ui.finances.purchases

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
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

    private var supplyList = emptyList<Supply>()
    private var selectedSupply: Supply? = null // Almacenar el insumo seleccionado

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSupplySpinner()
        setupSaveButton()
    }

    private fun setupSupplySpinner() {
        val preselectedId = arguments?.getLong("supplyId") ?: 0

        supplyViewModel.supplies.observe(viewLifecycleOwner) { supplies ->
            supplyList = supplies

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                supplies.map { it.name }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerSupply.adapter = adapter

            if (preselectedId != -1L) {
                val index = supplies.indexOfFirst { it.id == preselectedId }
                if (index >= 0) binding.spinnerSupply.setSelection(index)
            }

            binding.spinnerSupply.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (supplyList.isNotEmpty() && position in supplyList.indices) {
                        selectedSupply = supplyList[position]
                        updatePurchaseUnitSelector(selectedSupply) // Llama al nuevo método
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun setupSaveButton() {
        binding.btnSavePurchase.setOnClickListener {

            val selectedPosition = binding.spinnerSupply.selectedItemPosition
            val selectedSupply = if (selectedPosition >= 0 && selectedPosition < supplyList.size) {
                supplyList[selectedPosition]
            } else {
                null
            }

            val enteredQuantity = binding.inputQuantity.text.toString().toDoubleOrNull() ?: 0.0

            // Lógica para obtener la unidad de compra seleccionada del RadioGroup
            val checkedRadioButtonId = binding.rgPurchaseUnitSelector.checkedRadioButtonId
            val selectedRadioButton = view?.findViewById<RadioButton>(checkedRadioButtonId)
            val selectedUnit = selectedRadioButton?.text?.toString()

            val totalPrice = binding.inputTotalPrice.text.toString().toDoubleOrNull() ?: 0.0
            val supplier = binding.inputSupplier.text.toString().trim().ifEmpty { null }
            val notes = binding.inputNotes.text.toString().trim().ifEmpty { null }

            if (selectedSupply == null || selectedUnit == null || enteredQuantity <= 0.0 || totalPrice <= 0.0) {
                Toast.makeText(requireContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var finalQuantity = enteredQuantity

            // Usamos el 'selectedSupply' (insumo seleccionado)
            if (selectedUnit == selectedSupply.purchaseUnit && selectedSupply.conversionFactor != null && selectedSupply.conversionFactor > 0.0) {
                finalQuantity = enteredQuantity * selectedSupply.conversionFactor
            }

            lifecycleScope.launch {
                try {
                    viewModel.registerPurchase(
                        selectedSupply,
                        finalQuantity,
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

    private fun updatePurchaseUnitSelector(supply: Supply?) {
        // Limpiar el grupo de radio buttons
        binding.rgPurchaseUnitSelector.removeAllViews()

        supply?.let {
            val availableUnits = mutableListOf<String>()

            // 1. Agregar la unidad base (it.unit)
            availableUnits.add(it.unit)

            // 2. Agregar la unidad de compra si es diferente y existe
            if (!it.purchaseUnit.isNullOrEmpty() && it.purchaseUnit != it.unit) {
                availableUnits.add(it.purchaseUnit)
            }

            // 3. Crear y agregar los RadioButtons al RadioGroup
            availableUnits.forEachIndexed { index, unit ->
                val rb = RadioButton(requireContext()).apply {
                    id = View.generateViewId() // Generar un ID único para cada RadioButton
                    text = unit
                    layoutParams = RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = 16.dpToPx() // Opcional: añade espacio
                    }
                }
                binding.rgPurchaseUnitSelector.addView(rb)

                // Seleccionar el primero por defecto
                if (index == 0) {
                    rb.isChecked = true
                }
            }
        }
    }

    // Extensión simple para convertir dp a pixeles, necesaria si se usa margin en layoutParams
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}