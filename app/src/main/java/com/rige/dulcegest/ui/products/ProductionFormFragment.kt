package com.rige.dulcegest.ui.products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.data.db.entities.Product
import com.rige.dulcegest.data.db.entities.ProductionBatch
import com.rige.dulcegest.data.db.entities.ProductionConsumption
import com.rige.dulcegest.data.db.relations.ProductRecipeWithIngredient
import com.rige.dulcegest.databinding.FragmentProductionFormBinding
import com.rige.dulcegest.ui.viewmodels.IngredientViewModel
import com.rige.dulcegest.ui.viewmodels.ProductViewModel
import com.rige.dulcegest.ui.viewmodels.ProductionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

@AndroidEntryPoint
class ProductionFormFragment : Fragment() {

    private var _binding: FragmentProductionFormBinding? = null
    private val binding get() = _binding!!

    private val productViewModel: ProductViewModel by activityViewModels()
    private val productionViewModel: ProductionViewModel by activityViewModels()
    private val ingredientViewModel: IngredientViewModel by viewModels()

    private var productList = emptyList<Product>()
    private var ingredientList = emptyList<ProductRecipeWithIngredient>()
    private var adapter: IngredientUsageAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductionFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbarProductionForm
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

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
                loadIngredientsForProduct(product.id)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadIngredientsForProduct(productId: Long) {
        productViewModel.getRecipeWithIngredients(productId).observe(viewLifecycleOwner) { list ->
            ingredientList = list
            if (list.isNotEmpty()) {
                binding.sectionIngredients.visibility = View.VISIBLE
                adapter = IngredientUsageAdapter(list)
                binding.rvIngredients.layoutManager = LinearLayoutManager(requireContext())
                binding.rvIngredients.adapter = adapter
            } else {
                binding.sectionIngredients.visibility = View.GONE
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

        // 1️⃣ Obtener lista de ingredientes usados
        val ingredientUsages = adapter?.getQuantities()
            ?.filter { it.value > 0 } ?: emptyMap()

        if (ingredientUsages.isEmpty()) {
            Toast.makeText(requireContext(), "No hay ingredientes usados", Toast.LENGTH_SHORT).show()
            return
        }

        // 2️⃣ Calcular costos de ingredientes y total
        lifecycleScope.launch {
            val allIngredients = ingredientViewModel.getAllIngredientsOnce()
            var totalCost = 0.0
            val consumptions = mutableListOf<ProductionConsumption>()

            for ((ingredientId, usedQty) in ingredientUsages) {
                val ingredient = allIngredients.find { it.id == ingredientId }
                if (ingredient != null) {
                    val cost = usedQty * ingredient.avgCost
                    totalCost += cost

                    consumptions.add(
                        ProductionConsumption(
                            batchId = 0L,
                            ingredientId = ingredientId,
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

            // 4️⃣ Guardar en BD
            productionViewModel.insertBatch(batch, consumptions)

            // 5️⃣ Actualizar stock del producto terminado
            val newProductStock = selectedProduct.stockQty + qtyProduced
            productViewModel.update(selectedProduct.copy(stockQty = newProductStock))

            // 6️⃣ Descontar ingredientes del inventario
            consumptions.forEach { consumption ->
                ingredientViewModel.consumeStock(consumption.ingredientId, consumption.qtyUsed)
            }

            // 7️⃣ Mostrar resultado
            Toast.makeText(
                requireContext(),
                "Lote producido correctamente.\nCosto total: ${"%.2f".format(totalCost)}",
                Toast.LENGTH_LONG
            ).show()

            findNavController().navigateUp()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
