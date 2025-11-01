package com.rige.dulcegest.ui.products.productions

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.data.local.entities.Product
import com.rige.dulcegest.databinding.FragmentProductionFormBinding
import com.rige.dulcegest.domain.usecases.products.productions.SaveProductionUseCase
import com.rige.dulcegest.ui.common.BaseFragment
import com.rige.dulcegest.ui.products.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductionFormFragment :
    BaseFragment<FragmentProductionFormBinding>(FragmentProductionFormBinding::inflate) {

    override val toolbarTitle: String
        get() = if (args.batchId == 0L) "Registrar Producción" else "Editar Producción"

    override val showToolbar = true
    override val showBackButton = true

    // Se mantiene para obtener la lista de productos y la receta
    private val productViewModel: ProductViewModel by activityViewModels()
    private val productionViewModel: ProductionViewModel by activityViewModels()
    // ⚠️ SupplyViewModel ya NO se necesita aquí.

    private val args: ProductionFormFragmentArgs by navArgs()

    private var productList = emptyList<Product>()
    private var adapter: SupplyUsageAdapter? = null
    private var isEditMode = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isEditMode = args.batchId != 0L

        setupProductSpinner()
        setupListeners()

        if (isEditMode) {
            loadExistingBatch(args.batchId)
            binding.spinnerProduct.isEnabled = false
            binding.sectionSupplies.visibility = View.GONE
            binding.inputNotes.visibility = View.GONE
            binding.btnSave.text = "Actualizar cantidad"
        } else {
            // Observar el resultado del guardado solo en modo registro
            observeSaveResult()
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener { saveProduction() }
    }

    private fun setupProductSpinner() {
        productViewModel.products.observe(viewLifecycleOwner) { products ->
            productList = products
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                products.map { it.name }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerProduct.adapter = adapter

            // Si es edición, seleccionamos el producto correcto después de cargar la lista
            if (isEditMode) {
                // La carga del lote ya maneja la selección
            } else {
                binding.spinnerProduct.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val product = productList[position]
                        loadSuppliesForProduct(product.id)
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }
    }

    private fun loadSuppliesForProduct(productId: Long) {
        // Se mantiene la obtención de la receta para poblar el adaptador de la UI
        productViewModel.getRecipeWithSupplies(productId).observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                binding.sectionSupplies.visibility = View.VISIBLE
                adapter = SupplyUsageAdapter(list)
                binding.rvSupplies.layoutManager = LinearLayoutManager(requireContext())
                binding.rvSupplies.adapter = adapter
            } else {
                binding.sectionSupplies.visibility = View.GONE
            }
        }
    }

    private fun loadExistingBatch(batchId: Long) {
        productionViewModel.getBatchProductConsumptionByIdOnce(batchId).observe(viewLifecycleOwner) { batchWithProduct ->
            batchWithProduct?.let {
                val selectedIndex = productList.indexOfFirst { p -> p.id == it.batch.productId }
                if (selectedIndex >= 0) binding.spinnerProduct.setSelection(selectedIndex)

                binding.inputQuantity.setText(it.batch.quantityProduced.toString())
            }
        }
    }

    private fun observeSaveResult() {
        productionViewModel.saveResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SaveProductionUseCase.Result.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "Lote producido correctamente.\nCosto total: ${"%.2f".format(result.totalCost)}",
                        Toast.LENGTH_LONG
                    ).show()
                    findNavController().navigateUp()
                }
                is SaveProductionUseCase.Result.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun saveProduction() {
        val qtyProduced = binding.inputQuantity.text.toString().toDoubleOrNull()

        // 1. Validación de UI (Mínima)
        if (qtyProduced == null || qtyProduced <= 0) {
            Toast.makeText(requireContext(), "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show()
            return
        }

        if (isEditMode) {
            // 2. Lógica de EDICIÓN (Llamada simple al ViewModel)
            productionViewModel.updateBatchQuantity(args.batchId, qtyProduced)
            Toast.makeText(requireContext(), "Cantidad de lote actualizada", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        } else {
            // 3. Lógica de REGISTRO NUEVO (Llamada al Use Case a través del ViewModel)
            if (productList.isEmpty()) return
            val selectedProduct = productList[binding.spinnerProduct.selectedItemPosition]
            val supplyUsages = adapter?.getQuantities() ?: emptyMap()
            val notes = binding.inputNotes.text.toString().ifEmpty { null }

            // El Use Case dentro del ViewModel valida los insumos y realiza toda la transacción
            productionViewModel.registerNewProduction(
                selectedProduct,
                qtyProduced,
                supplyUsages,
                notes
            )
        }
    }
}