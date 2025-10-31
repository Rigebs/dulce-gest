package com.rige.dulcegest.ui.finances.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rige.dulcegest.data.local.entities.ShoppingListItem
import com.rige.dulcegest.data.local.entities.Supply
import com.rige.dulcegest.databinding.DialogAddShoppingItemBinding
import com.rige.dulcegest.ui.products.supplies.SupplyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddShoppingItemDialog : BottomSheetDialogFragment() {

    private var _binding: DialogAddShoppingItemBinding? = null
    private val binding get() = _binding!!

    private val supplyViewModel: SupplyViewModel by viewModels()
    private val shoppingListViewModel: ShoppingListViewModel by viewModels()

    private var supplies: List<Supply> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddShoppingItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        supplyViewModel.supplies.observe(viewLifecycleOwner) { list ->
            supplies = list
            val names = list.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerSupply.adapter = adapter
        }

        binding.btnSave.setOnClickListener {
            val selectedIndex = binding.spinnerSupply.selectedItemPosition
            if (selectedIndex == AdapterView.INVALID_POSITION) {
                Toast.makeText(requireContext(), "Selecciona un insumo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedSupply = supplies[selectedIndex]
            val quantity = binding.etQuantity.text.toString().toDoubleOrNull()
            val unit = binding.etUnit.text.toString()

            if (quantity == null || unit.isBlank()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newItem = ShoppingListItem(
                supplyId = selectedSupply.id,
                quantity = quantity,
                unit = unit,
                purchased = false
            )

            shoppingListViewModel.addItem(newItem)
            dismiss()
        }

        binding.btnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}