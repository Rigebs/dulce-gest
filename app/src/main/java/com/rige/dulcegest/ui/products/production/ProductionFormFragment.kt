package com.rige.dulcegest.ui.products.production

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.data.local.entities.Product
import com.rige.dulcegest.data.local.entities.ProductionBatch
import com.rige.dulcegest.data.local.entities.ProductionConsumption
import com.rige.dulcegest.data.local.entities.relations.ProductRecipeWithSupply
import com.rige.dulcegest.databinding.FragmentProductionFormBinding
import com.rige.dulcegest.ui.common.BaseFragment
import com.rige.dulcegest.ui.products.supplies.SupplyViewModel
import com.rige.dulcegest.ui.products.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

@AndroidEntryPoint
class ProductionFormFragment :
    BaseFragment<FragmentProductionFormBinding>(FragmentProductionFormBinding::inflate) {

    override val toolbarTitle = "Registrar Producción"
    override val showToolbar = true
    override val showBackButton = true

    private val productViewModel: ProductViewModel by activityViewModels()
    private val productionViewModel: ProductionViewModel by activityViewModels()
    private val supplyViewModel: SupplyViewModel by viewModels()

    private var productList = emptyList<Product>()
    private var supplyList = emptyList<ProductRecipeWithSupply>()
    private var adapter: SupplyUsageAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProductSpinner()
        binding.btnSave.setOnClickListener { saveProduction() }
    }
    private fun setupProductSpinner() {
        productViewModel.products.observe(viewLifecycleOwner) { products ->
            productList = products
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, products.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerProduct.adapter = adapter
        }

        binding.spinnerProduct.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val product = productList[position]
                loadSuppliesForProduct(product.id)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadSuppliesForProduct(productId: Long) {
        productViewModel.getRecipeWithSupplies(productId).observe(viewLifecycleOwner) { list ->
            supplyList = list
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

    private fun saveProduction() {
        if (productList.isEmpty()) return

        val selectedProduct = productList[binding.spinnerProduct.selectedItemPosition]
        val qtyProduced = binding.inputQuantity.text.toString().toDoubleOrNull()
        if (qtyProduced == null || qtyProduced <= 0) {
            Toast.makeText(requireContext(), "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show()
            return
        }

        val supplyUsages = adapter?.getQuantities()
            ?.filter { it.value > 0 } ?: emptyMap()

        if (supplyUsages.isEmpty()) {
            Toast.makeText(requireContext(), "No hay insumos usados", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val allSupplies = supplyViewModel.getAllSuppliesOnce()
            var totalCost = 0.0
            val consumptions = mutableListOf<ProductionConsumption>()

            for ((supplyId, usedQty) in supplyUsages) {
                val supply = allSupplies.find { it.id == supplyId }
                if (supply != null) {
                    val cost = usedQty * supply.avgCost
                    totalCost += cost

                    consumptions.add(
                        ProductionConsumption(
                            batchId = 0L,
                            supplyId = supplyId,
                            qtyUsed = usedQty,
                            cost = cost
                        )
                    )
                }
            }

            // 3️⃣ Crear lote con costo total calculado
            val batch = ProductionBatch(
                productId = selectedProduct.id,
                quantityProduced = qtyProduced,
                totalCost = totalCost,
                date = LocalDateTime.now().toString(),
                notes = binding.inputNotes.text.toString().ifEmpty { null }
            )

            productionViewModel.insertBatch(batch, consumptions)

            val newProductStock = selectedProduct.stockQty + qtyProduced
            productViewModel.update(selectedProduct.copy(stockQty = newProductStock))

            consumptions.forEach { consumption ->
                supplyViewModel.consumeStock(consumption.supplyId, consumption.qtyUsed)
            }

            Toast.makeText(
                requireContext(),
                "Lote producido correctamente.\nCosto total: ${"%.2f".format(totalCost)}",
                Toast.LENGTH_LONG
            ).show()

            findNavController().navigateUp()
        }
    }
}
