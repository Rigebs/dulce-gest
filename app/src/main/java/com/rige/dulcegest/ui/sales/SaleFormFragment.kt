package com.rige.dulcegest.ui.sales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.data.db.entities.Product
import com.rige.dulcegest.data.db.entities.Sale
import com.rige.dulcegest.data.db.entities.SaleItem
import com.rige.dulcegest.data.db.relations.SaleItemWithProduct
import com.rige.dulcegest.databinding.FragmentSaleFormBinding
import com.rige.dulcegest.ui.viewmodels.ProductViewModel
import com.rige.dulcegest.ui.viewmodels.SaleViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

@AndroidEntryPoint
class SaleFormFragment : Fragment() {

    private var _binding: FragmentSaleFormBinding? = null
    private val binding get() = _binding!!

    private val saleViewModel: SaleViewModel by viewModels()
    private val productViewModel: ProductViewModel by viewModels()

    private lateinit var adapter: SaleItemAdapter
    private val selectedItems = mutableListOf<SaleItemWithProduct>()
    private var products: List<Product> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSaleFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbarSaleForm
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val saleId = arguments?.getLong("saleId")
        toolbar.title = if (saleId == null) "Registrar venta" else "Editar venta"

        adapter = SaleItemAdapter(
            onRemove = { item ->
                selectedItems.remove(item)
                adapter.submitList(selectedItems.toList())
                updateTotal()
            }
        )
        binding.recyclerSaleItems.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSaleItems.adapter = adapter

        // 🧮 Cargar productos y configurar el selector
        productViewModel.products.observe(viewLifecycleOwner) { list ->
            products = list
            val displayNames = list.map { it.name }

            val productAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                displayNames
            )

            binding.autoProduct.apply {
                setAdapter(productAdapter)
                threshold = 0
                setOnClickListener { showDropDown() }
                setOnItemClickListener { _, _, position, _ ->
                    tag = list[position]
                }
            }
        }

        // ➕ Agregar producto a la venta
        binding.btnAddProduct.setOnClickListener {
            val selectedProduct = binding.autoProduct.tag as? Product ?: return@setOnClickListener
            val qty = binding.etQuantity.text.toString().toDoubleOrNull() ?: 0.0
            if (qty <= 0) {
                Toast.makeText(requireContext(), "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Evitar duplicados
            if (selectedItems.any { it.product.id == selectedProduct.id }) {
                Toast.makeText(requireContext(), "Este producto ya fue agregado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val unitPrice = selectedProduct.price
            val item = SaleItem(
                saleId = 0L,
                productId = selectedProduct.id,
                qty = qty,
                unitPrice = unitPrice,
                lineTotal = qty * unitPrice
            )
            val itemWithProduct = SaleItemWithProduct(item, selectedProduct)

            selectedItems.add(itemWithProduct)
            adapter.submitList(selectedItems.toList())
            updateTotal()

            // limpiar campos
            binding.etQuantity.text?.clear()
            binding.autoProduct.setText("")
            binding.autoProduct.tag = null
        }

        // 💾 Guardar venta
        binding.btnSave.setOnClickListener {
            if (selectedItems.isEmpty()) {
                Toast.makeText(requireContext(), "Agrega al menos un producto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sale = Sale(
                saleDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                totalAmount = selectedItems.sumOf { it.item.lineTotal },
                customer = binding.etCustomer.text.toString()
            )

            val saleItems = selectedItems.map { it.item }

            saleViewModel.insertSale(sale, saleItems)
            Toast.makeText(requireContext(), "Venta registrada correctamente", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        updateTotal()
    }

    private fun updateTotal() {
        val total = selectedItems.sumOf { it.item.lineTotal }
        binding.tvTotal.text = "Total: $${"%.2f".format(total)}"

        if (selectedItems.isEmpty()) {
            binding.recyclerSaleItems.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        } else {
            binding.recyclerSaleItems.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}