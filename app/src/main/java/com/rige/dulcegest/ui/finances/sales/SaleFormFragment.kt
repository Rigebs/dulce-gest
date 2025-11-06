package com.rige.dulcegest.ui.finances.sales

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.core.utils.toSoles
import com.rige.dulcegest.data.local.entities.Product
import com.rige.dulcegest.data.local.entities.Sale
import com.rige.dulcegest.data.local.entities.SaleItem
import com.rige.dulcegest.data.local.entities.relations.SaleItemWithProduct
import com.rige.dulcegest.databinding.FragmentSaleFormBinding
import com.rige.dulcegest.domain.models.SelectableSaleItem
import com.rige.dulcegest.ui.common.BaseFragment
import com.rige.dulcegest.ui.products.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

@AndroidEntryPoint
class SaleFormFragment :
    BaseFragment<FragmentSaleFormBinding>(FragmentSaleFormBinding::inflate) {

    override val toolbarTitle = "Registrar venta"
    override val showToolbar = true
    override val showBackButton = true

    private val saleViewModel: SaleViewModel by viewModels()
    private val productViewModel: ProductViewModel by viewModels()

    private lateinit var adapter: SaleItemAdapter
    private val selectedItems = mutableListOf<SaleItemWithProduct>()
    private val baseProducts = mutableListOf<Product>()
    private val allSelectableItems = mutableListOf<SelectableSaleItem>()
    private var selectedPaymentMethod: String = "Efectivo"

    private var currentSelectedItem: SelectableSaleItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        observeProducts()
        setupPaymentMethodSelector()
        setupAddButton()
        setupSaveButton()
        updateTotal()
    }

    private fun setupPaymentMethodSelector() {
        // Inicializar con el valor por defecto (Efectivo) al inicio
        binding.rgPaymentMethod.check(binding.rbCash.id)

        binding.rgPaymentMethod.setOnCheckedChangeListener { group, checkedId ->
            selectedPaymentMethod = when (checkedId) {
                binding.rbCash.id -> "Efectivo"
                binding.rbPlinYape.id -> "Plin/Yape"
                binding.rbOther.id -> "Otro"
                else -> "Desconocido"
            }
        }
    }

    private fun setupRecycler() {
        adapter = SaleItemAdapter(
            onQuantityChange = { item, newQty ->
                if (newQty <= 0.0) {
                    selectedItems.remove(item)
                } else {
                    val index = selectedItems.indexOf(item)
                    if (index != -1) {
                        val updatedItem = item.item.copy(
                            qty = newQty,
                            lineTotal = newQty * item.item.unitPrice
                        )
                        selectedItems[index] = item.copy(item = updatedItem)
                    }
                }

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
            allSelectableItems.clear()

            val placeholder = SelectableSaleItem(
                productId = -1L,
                name = "Seleccione un producto",
                price = 0.0
            )
            allSelectableItems.add(placeholder)

            list.forEach { productWithPresentation ->
                val product = productWithPresentation.product
                baseProducts.add(product)

                allSelectableItems.add(
                    SelectableSaleItem(
                        productId = product.id,
                        name = "${product.name} (Unidad)",
                        price = product.price
                    )
                )

                productWithPresentation.presentations.forEach { presentation ->
                    allSelectableItems.add(
                        SelectableSaleItem(
                            productId = product.id,
                            presentationId = presentation.id,
                            name = "${product.name} (${presentation.name})",
                            price = presentation.price,
                            presentationQuantity = presentation.quantity
                        )
                    )
                }
            }

            // 2. Configurar el ÚNICO Spinner
            val itemNames = allSelectableItems.map {
                if (it.productId == -1L) it.name else "${it.name} — S/${"%.2f".format(it.price)}"
            }

            val productAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                itemNames
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
            }

            binding.spinnerProduct.adapter = productAdapter

            binding.spinnerProduct.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    currentSelectedItem = if (position == 0) null else allSelectableItems[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    currentSelectedItem = null
                }
            }
        }
    }

    private fun setupAddButton() {
        binding.btnAddProduct.setOnClickListener {
            val selected = currentSelectedItem
            if (selected == null || selected.productId == -1L) {
                Toast.makeText(requireContext(), "Seleccione un producto y presentación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val qty = binding.etQuantity.text.toString().toDoubleOrNull() ?: 0.0
            if (qty <= 0) {
                Toast.makeText(requireContext(), "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val item = SaleItem(
                saleId = 0L,
                productId = selected.productId,
                presentationId = selected.presentationId,
                qty = qty,
                unitPrice = selected.price,
                lineTotal = qty * selected.price,
                presentationQuantity = selected.presentationQuantity
            )

            val displayProduct = Product(
                id = selected.productId,
                name = selected.name,
                price = selected.price
            )
            val itemWithProduct = SaleItemWithProduct(item, displayProduct)
            selectedItems.add(itemWithProduct)

            adapter.submitList(selectedItems.toList())
            updateTotal()

            binding.etQuantity.text?.clear()
            binding.spinnerProduct.setSelection(0)
            currentSelectedItem = null
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
                customer = binding.etCustomer.text.toString(),
                paymentMethod = selectedPaymentMethod
            )

            binding.btnSave.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                saleViewModel.insertSale(sale, selectedItems.map { it.item })
                Toast.makeText(requireContext(), "Venta registrada correctamente", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun updateTotal() {
        val total = selectedItems.sumOf { it.item.lineTotal }
        binding.tvTotal.text = "Total: ${total.toSoles()}"
        binding.recyclerSaleItems.visibility = if (selectedItems.isEmpty()) View.GONE else View.VISIBLE
        binding.emptyStateLayout.visibility = if (selectedItems.isEmpty()) View.VISIBLE else View.GONE
    }
}
