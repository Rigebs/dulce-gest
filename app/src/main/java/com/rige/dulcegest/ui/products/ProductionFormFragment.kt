package com.rige.dulcegest.ui.products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rige.dulcegest.data.db.entities.Product
import com.rige.dulcegest.data.db.entities.ProductionBatch
import com.rige.dulcegest.databinding.FragmentProductionFormBinding
import com.rige.dulcegest.ui.viewmodels.ProductViewModel
import com.rige.dulcegest.ui.viewmodels.ProductionViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDateTime


@AndroidEntryPoint
class ProductionFormFragment : Fragment() {

    private var _binding: FragmentProductionFormBinding? = null
    private val binding get() = _binding!!

    private val productViewModel: ProductViewModel by activityViewModels()
    private val productionViewModel: ProductionViewModel by activityViewModels()

    private var productList: List<Product> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductionFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbarProductionForm
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupProductSpinner()
        binding.btnSave.setOnClickListener { saveProduction() }
    }

    private fun setupProductSpinner() {
        productViewModel.products.observe(viewLifecycleOwner) { products ->
            productList = products

            val productNames = products.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, productNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerProduct.adapter = adapter
        }
    }

    private fun saveProduction() {
        if (productList.isEmpty()) {
            Toast.makeText(requireContext(), "No hay productos disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedIndex = binding.spinnerProduct.selectedItemPosition
        val selectedProduct = productList[selectedIndex]

        val quantityProduced = binding.inputQuantity.text.toString().toDoubleOrNull()
        val totalCost = binding.inputTotalCost.text.toString().toDoubleOrNull() ?: 0.0
        val notes = binding.inputNotes.text.toString().trim().ifEmpty { null }

        if (quantityProduced == null || quantityProduced <= 0) {
            Toast.makeText(requireContext(), "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show()
            return
        }

        val productionBatch = ProductionBatch(
            productId = selectedProduct.id,
            quantityProduced = quantityProduced,
            totalCost = totalCost,
            date = LocalDateTime.now().toString(),
            notes = notes
        )

        productionViewModel.saveBatch(productionBatch).observe(viewLifecycleOwner) { success ->
            if (success) {
                val newStock = selectedProduct.stockQty + quantityProduced
                val updatedProduct = selectedProduct.copy(stockQty = newStock)
                productViewModel.update(updatedProduct)

                Toast.makeText(requireContext(), "Producción registrada y stock actualizado", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
