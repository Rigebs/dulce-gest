package com.rige.dulcegest.ui.sales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.data.db.entities.Product
import com.rige.dulcegest.data.db.entities.ProductPresentation
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

    private val baseProducts = mutableListOf<Product>()
    private val productPresentations = mutableMapOf<Long, List<ProductPresentation>>()

    private var selectedProduct: Product? = null
    private var selectedPresentation: ProductPresentation? = null

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
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        toolbar.title = "Registrar venta"

        setupRecycler()
        observeProducts()
        setupAddButton()
        setupSaveButton()
        updateTotal()
    }

    private fun setupRecycler() {
        adapter = SaleItemAdapter(
            onRemove = { item ->
                selectedItems.remove(item)
                adapter.submitList(selectedItems.toList())
                updateTotal()
            }
        )
        binding.recyclerSaleItems.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSaleItems.adapter = adapter
    }

    private fun observeProducts() {
        productViewModel.productsWithPresentations.observe(viewLifecycleOwner) { list ->
            baseProducts.clear()
            productPresentations.clear()

            list.forEach {
                baseProducts.add(it.product)
                productPresentations[it.product.id] = it.presentations
            }

            val productNames = mutableListOf("Seleccione un producto")
            productNames.addAll(baseProducts.map { it.name })

            // 🔹 Adapter con dos layouts (uno para vista cerrada y otro para desplegable)
            val productAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                productNames
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
            }

            binding.spinnerProduct.adapter = productAdapter

            binding.spinnerProduct.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position == 0) {
                        selectedProduct = null
                        binding.spinnerPresentation.visibility = View.GONE
                        return
                    }

                    selectedProduct = baseProducts[position - 1]
                    selectedPresentation = null
                    setupPresentationSpinner(selectedProduct!!)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedProduct = null
                }
            }
        }
    }

    private fun setupPresentationSpinner(product: Product) {
        val presentations = productPresentations[product.id].orEmpty()

        if (presentations.isEmpty()) {
            binding.spinnerPresentation.visibility = View.GONE
            selectedPresentation = null
            return
        }

        val presentationNames = mutableListOf("Unidad — S/${product.price}")
        presentationNames.addAll(presentations.map { "${it.name} — S/${it.price}" })

        // 🔹 Igual configuración que en el spinner de productos
        val presentationAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            presentationNames
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        }

        binding.spinnerPresentation.apply {
            adapter = presentationAdapter
            visibility = View.VISIBLE
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedPresentation = if (position == 0) null else presentations[position - 1]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedPresentation = null
                }
            }
        }
    }

    private fun setupAddButton() {
        binding.btnAddProduct.setOnClickListener {
            val base = selectedProduct
            if (base == null) {
                Toast.makeText(requireContext(), "Seleccione un producto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val qty = binding.etQuantity.text.toString().toDoubleOrNull() ?: 0.0
            if (qty <= 0) {
                Toast.makeText(requireContext(), "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = selectedPresentation?.price ?: base.price
            val item = SaleItem(
                saleId = 0L,
                productId = base.id,
                qty = qty,
                unitPrice = price,
                lineTotal = qty * price
            )

            val itemWithProduct = SaleItemWithProduct(item, base)
            selectedItems.add(itemWithProduct)

            adapter.submitList(selectedItems.toList())
            updateTotal()

            // limpiar campos
            binding.etQuantity.text?.clear()
            binding.spinnerProduct.setSelection(0)
            binding.spinnerPresentation.visibility = View.GONE
            selectedProduct = null
            selectedPresentation = null
        }
    }

    private fun setupSaveButton() {
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

            saleViewModel.insertSale(sale, selectedItems.map { it.item })
            Toast.makeText(requireContext(), "Venta registrada correctamente", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun updateTotal() {
        val total = selectedItems.sumOf { it.item.lineTotal }
        binding.tvTotal.text = "Total: S/${"%.2f".format(total)}"
        binding.recyclerSaleItems.visibility = if (selectedItems.isEmpty()) View.GONE else View.VISIBLE
        binding.emptyStateLayout.visibility = if (selectedItems.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
