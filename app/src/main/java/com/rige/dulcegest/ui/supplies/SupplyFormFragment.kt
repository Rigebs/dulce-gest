package com.rige.dulcegest.ui.supplies

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
import com.rige.dulcegest.data.db.entities.Supply
import com.rige.dulcegest.databinding.FragmentSupplyFormBinding
import com.rige.dulcegest.ui.viewmodels.SupplyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

@AndroidEntryPoint
class SupplyFormFragment : Fragment(R.layout.fragment_supply_form) {

    private var _binding: FragmentSupplyFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SupplyViewModel by viewModels()
    private var supplyId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupplyFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbarSupplyForm
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        val unitsAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.units_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        }
        binding.spinnerUnit.adapter = unitsAdapter

        supplyId = arguments?.getLong("supplyId")

        toolbar.title = if (supplyId == null || supplyId == 0L)
            "Crear insumo" else "Editar insumo"

        supplyId?.let { id ->
            viewModel.getSupplyById(id).observe(viewLifecycleOwner) { supply ->
                supply?.let {
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

        binding.btnSave.setOnClickListener { saveSupply() }
    }

    private fun saveSupply() {
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

        val supply = Supply(
            id = supplyId ?: 0L,
            name = name,
            unit = unit,
            stockQty = stock,
            purchaseUnit = purchaseUnit,
            conversionFactor = conversionFactor,
            updatedAt = LocalDateTime.now().toString(),
            notes = notes
        )

        lifecycleScope.launch {
            if (supplyId == null || supplyId == 0L) {
                viewModel.insert(supply)
                Toast.makeText(requireContext(), "Insumo agregado", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.update(supply)
                Toast.makeText(requireContext(), "Insumo actualizado", Toast.LENGTH_SHORT).show()
            }
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}