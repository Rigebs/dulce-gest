package com.rige.dulcegest.ui.products.form

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.RoundedCornersTransformation
import com.rige.dulcegest.R
import com.rige.dulcegest.data.local.entities.Product
import com.rige.dulcegest.data.local.entities.ProductPresentation
import com.rige.dulcegest.data.local.entities.ProductRecipe
import com.rige.dulcegest.data.local.entities.ProductVariant
import com.rige.dulcegest.data.local.entities.Supply
import com.rige.dulcegest.data.local.entities.relations.ProductRecipeWithSupply
import com.rige.dulcegest.databinding.FragmentProductFormBinding
import com.rige.dulcegest.ui.common.BaseFragment
import com.rige.dulcegest.ui.products.ProductViewModel
import com.rige.dulcegest.ui.products.supplies.SupplyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

@AndroidEntryPoint
class ProductFormFragment :
    BaseFragment<FragmentProductFormBinding>(FragmentProductFormBinding::inflate) {

    override val toolbarTitle: String
        get() = if (productId == 0L) "Registrar producto" else "Editar producto"

    override val showToolbar = true
    override val showBackButton = true

    private val productViewModel: ProductViewModel by activityViewModels()
    private val supplyViewModel: SupplyViewModel by activityViewModels()

    private val supplyAdapter: RecipeSupplyAdapter by lazy {
        RecipeSupplyAdapter { recipeItem -> removeSupply(recipeItem) }
    }
    private val presentationAdapter: ProductPresentationAdapter by lazy {
        ProductPresentationAdapter { presentation -> removePresentation(presentation) }
    }
    private val variantAdapter: ProductVariantAdapter by lazy {
        ProductVariantAdapter { variant -> removeVariant(variant) }
    }

    private var productId: Long = 0L
    private var availableSupplies: List<Supply> = emptyList()
    private lateinit var unitsAdapter: ArrayAdapter<CharSequence>

    private var currentProduct: Product? = null
    private var selectedImagePath: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val path = copyImageToInternalStorage(it)
            path?.let { localPath ->
                selectedImagePath = localPath
                binding.imageProduct.load(File(localPath)) {
                    placeholder(R.drawable.ic_placeholder_image)
                    error(R.drawable.ic_placeholder_image)
                    transformations(RoundedCornersTransformation(12f))
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productId = arguments?.getLong("productId") ?: 0L

        setupSpinners()
        setupImageSelector()
        setupRecyclerViews()
        setupListeners()
        observeData()

        if (productId == 0L) {
            updatePresentationEmptyState()
            updateVariantEmptyState()
            updateSupplyEmptyState()
        }
    }

    private fun setupSpinners() {
        unitsAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.units_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        }
        binding.spinnerUnit.adapter = unitsAdapter
    }

    private fun setupRecyclerViews() {
        binding.recyclerSupplies.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = supplyAdapter
        }
        binding.recyclerPresentations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = presentationAdapter
        }
        binding.recyclerVariants.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = variantAdapter
        }
    }

    private fun setupListeners() {
        binding.btnAddSupply.setOnClickListener { showSupplySelector() }
        binding.btnAddPresentation.setOnClickListener { showAddPresentationDialog() }
        binding.btnAddVariant.setOnClickListener { showAddVariantDialog() }
        binding.btnSave.setOnClickListener { saveProduct() }
    }

    private fun observeData() {
        supplyViewModel.supplies.observe(viewLifecycleOwner) {
            availableSupplies = it
        }

        if (productId != 0L) {
            productViewModel.getProductById(productId).observe(viewLifecycleOwner) { product ->
                product?.let {
                    currentProduct = it
                    bindProductDetails(it)
                }
            }

            productViewModel.getRecipeWithSupplies(productId).observe(viewLifecycleOwner) { items ->
                supplyAdapter.setItems(items)
                updateSupplyEmptyState()
            }

            productViewModel.getPresentationsByProduct(productId).observe(viewLifecycleOwner) { presentations ->
                presentationAdapter.setItems(presentations)
                updatePresentationEmptyState()
            }

            productViewModel.getVariantsByProduct(productId).observe(viewLifecycleOwner) { variants ->
                variantAdapter.setItems(variants)
                updateVariantEmptyState()
            }
        }
    }

    private fun bindProductDetails(product: Product) {
        binding.inputName.setText(product.name)

        val unitPosition = unitsAdapter.getPosition(product.unit)
        if (unitPosition >= 0) binding.spinnerUnit.setSelection(unitPosition)

        binding.inputPrice.setText(product.price.toString())
        binding.inputNotes.setText(product.notes ?: "")

        product.imagePath?.let { path ->
            selectedImagePath = path
            binding.imageProduct.load(File(path)) {
                placeholder(R.drawable.ic_placeholder_image)
                error(R.drawable.ic_placeholder_image)
                transformations(RoundedCornersTransformation(12f))
            }
        }
    }

    private fun removeSupply(recipeItem: ProductRecipeWithSupply) {
        supplyAdapter.removeSupply(recipeItem) {
            updateSupplyEmptyState()
        }
    }

    private fun removePresentation(presentation: ProductPresentation) {
        presentationAdapter.removePresentation(presentation) {
            updatePresentationEmptyState()
        }
    }

    private fun removeVariant(variant: ProductVariant) {
        variantAdapter.removeVariant(variant) {
            updateVariantEmptyState()
        }
    }

    private fun updateSupplyEmptyState() {
        binding.recyclerSupplies.visibility = if (supplyAdapter.itemCount == 0) View.GONE else View.VISIBLE
        binding.emptyStateLayout.visibility = if (supplyAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun updatePresentationEmptyState() {
        binding.recyclerPresentations.visibility = if (presentationAdapter.itemCount == 0) View.GONE else View.VISIBLE
        binding.emptyPresentationState.visibility = if (presentationAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun updateVariantEmptyState() {
        binding.recyclerVariants.visibility = if (variantAdapter.itemCount == 0) View.GONE else View.VISIBLE
        binding.emptyVariantState.visibility = if (variantAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun showSupplySelector() {
        if (availableSupplies.isEmpty()) {
            Toast.makeText(requireContext(), "No hay insumos disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val supplyNames = availableSupplies.map { it.name }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona un insmo")
            .setItems(supplyNames) { _, which ->
                val selectedSupply = availableSupplies[which]
                val recipeItem = ProductRecipeWithSupply(
                    recipe = ProductRecipe(
                        id = 0,
                        productId = productId,
                        supplyId = selectedSupply.id,
                        qtyPerUnit = 0.0
                    ),
                    supply = selectedSupply
                )
                supplyAdapter.addSupply(recipeItem) {
                    updateSupplyEmptyState()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showAddPresentationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_presentation, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.inputPresentationName)
        val quantityInput = dialogView.findViewById<EditText>(R.id.inputPresentationQuantity)
        val priceInput = dialogView.findViewById<EditText>(R.id.inputPresentationPrice)

        AlertDialog.Builder(requireContext())
            .setTitle("Agregar presentación")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val name = nameInput.text.toString().trim()
                val price = priceInput.text.toString().toDoubleOrNull() ?: 0.0
                val quantity = quantityInput.text.toString().toDoubleOrNull() ?: 0.0

                if (name.isNotEmpty() && price > 0) {
                    val presentation = ProductPresentation(
                        id = 0,
                        productId = productId,
                        name = name,
                        quantity = quantity,
                        price = price
                    )
                    presentationAdapter.addPresentation(presentation) {
                        updatePresentationEmptyState()
                    }
                } else {
                    Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showAddVariantDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_variant, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.inputVariantName)
        val priceInput = dialogView.findViewById<EditText>(R.id.inputVariantPrice)

        AlertDialog.Builder(requireContext())
            .setTitle("Agregar variante")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val name = nameInput.text.toString().trim()
                val price = priceInput.text.toString().toDoubleOrNull() ?: 0.0

                if (name.isNotEmpty() && price > 0) {
                    val variant = ProductVariant(
                        id = 0,
                        productId = productId,
                        name = name,
                        price = price
                    )
                    variantAdapter.addVariant(variant) {
                        updateVariantEmptyState()
                    }
                } else {
                    Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun saveProduct() {
        val name = binding.inputName.text.toString().trim()
        val unit = binding.spinnerUnit.selectedItem.toString().ifEmpty { "u" }
        val price = binding.inputPrice.text.toString().toDoubleOrNull() ?: 0.0
        val notes = binding.inputNotes.text.toString().trim().ifEmpty { null }

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        // 🟢 Recolectar listas finales desde los adapters
        val recipeList = supplyAdapter.getItems().map { it.recipe }
        val presentations = presentationAdapter.getItems()
        val variants = variantAdapter.getItems()

        // 🟢 Llamada al ViewModel con los datos recolectados, delegando la lógica al Use Case
        productViewModel.saveProduct(
            currentProduct, // Pasa el objeto Product base (null si es nuevo)
            name,
            unit,
            price,
            notes,
            selectedImagePath,
            recipeList,
            presentations,
            variants
        )
            .observe(viewLifecycleOwner) { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Producto guardado", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(requireContext(), "Error al guardar el producto", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupImageSelector() {
        binding.btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun copyImageToInternalStorage(uri: Uri): String? {
        val filename = "product_${System.currentTimeMillis()}.jpg"
        val file = File(requireContext().filesDir, filename)
        try {
            requireContext().contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}