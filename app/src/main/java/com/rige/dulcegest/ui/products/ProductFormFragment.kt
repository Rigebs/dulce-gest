package com.rige.dulcegest.ui.products

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.RoundedCornersTransformation
import com.rige.dulcegest.R
import com.rige.dulcegest.data.db.entities.Ingredient
import com.rige.dulcegest.data.db.entities.Product
import com.rige.dulcegest.data.db.entities.ProductPresentation
import com.rige.dulcegest.data.db.entities.ProductRecipe
import com.rige.dulcegest.data.db.entities.ProductVariant
import com.rige.dulcegest.data.db.relations.ProductRecipeWithIngredient
import com.rige.dulcegest.databinding.FragmentProductFormBinding
import com.rige.dulcegest.ui.viewmodels.IngredientViewModel
import com.rige.dulcegest.ui.viewmodels.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

@AndroidEntryPoint
class ProductFormFragment : Fragment() {

    private var _binding: FragmentProductFormBinding? = null
    private val binding get() = _binding!!

    private val productViewModel: ProductViewModel by activityViewModels()
    private val ingredientViewModel: IngredientViewModel by activityViewModels()

    private val ingredientAdapter: RecipeIngredientAdapter by lazy {
        RecipeIngredientAdapter { recipeItem ->
            removeIngredient(recipeItem)
        }
    }
    private val presentationAdapter: ProductPresentationAdapter by lazy {
        ProductPresentationAdapter { presentation ->
            removePresentation(presentation)
        }
    }
    private val variantAdapter: ProductVariantAdapter by lazy {
        ProductVariantAdapter { variant ->
            removeVariant(variant)
        }
    }

    private var productId: Long = 0L
    private var availableIngredients: List<Ingredient> = emptyList()

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productId = arguments?.getLong("productId") ?: 0L

        setupToolbar()
        setupSpinners()
        setupImageSelector()
        setupRecyclerViews()
        setupListeners()
        observeData()

        if (productId == 0L) {
            updatePresentationEmptyState()
            updateVariantEmptyState()
            updateIngredientEmptyState()
        }
    }

    private fun setupToolbar() {
        binding.toolbarProductForm.apply {
            setNavigationOnClickListener { findNavController().navigateUp() }
            title = when (productId) {
                0L -> "Registrar Producto"
                else -> "Editar Producto"
            }
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
        binding.recyclerIngredients.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ingredientAdapter
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
        binding.btnAddIngredient.setOnClickListener { showIngredientSelector() }
        binding.btnAddPresentation.setOnClickListener { showAddPresentationDialog() }
        binding.btnAddVariant.setOnClickListener { showAddVariantDialog() }
        binding.btnSave.setOnClickListener { saveProduct() }
    }

    private fun observeData() {
        ingredientViewModel.ingredients.observe(viewLifecycleOwner) {
            availableIngredients = it
        }

        if (productId != 0L) {
            productViewModel.getProductById(productId).observe(viewLifecycleOwner) { product ->
                product?.let {
                    currentProduct = it
                    bindProductDetails(it)
                }
            }

            productViewModel.getRecipeWithIngredients(productId).observe(viewLifecycleOwner) { items ->
                ingredientAdapter.setItems(items)
                updateIngredientEmptyState()
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

    private fun removeIngredient(recipeItem: ProductRecipeWithIngredient) {
        ingredientAdapter.removeIngredient(recipeItem) {
            updateIngredientEmptyState()
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

    private fun updateIngredientEmptyState() {
        binding.recyclerIngredients.visibility = if (ingredientAdapter.itemCount == 0) View.GONE else View.VISIBLE
        binding.emptyStateLayout.visibility = if (ingredientAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun updatePresentationEmptyState() {
        binding.recyclerPresentations.visibility = if (presentationAdapter.itemCount == 0) View.GONE else View.VISIBLE
        binding.emptyPresentationState.visibility = if (presentationAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun updateVariantEmptyState() {
        binding.recyclerVariants.visibility = if (variantAdapter.itemCount == 0) View.GONE else View.VISIBLE
        binding.emptyVariantState.visibility = if (variantAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun showIngredientSelector() {
        if (availableIngredients.isEmpty()) {
            Toast.makeText(requireContext(), "No hay ingredientes disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val ingredientNames = availableIngredients.map { it.name }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona un ingrediente")
            .setItems(ingredientNames) { _, which ->
                val selectedIngredient = availableIngredients[which]
                val recipeItem = ProductRecipeWithIngredient(
                    recipe = ProductRecipe(
                        id = 0,
                        productId = productId,
                        ingredientId = selectedIngredient.id,
                        qtyPerUnit = 0.0
                    ),
                    ingredient = selectedIngredient
                )
                ingredientAdapter.addIngredient(recipeItem) {
                    updateIngredientEmptyState()
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

        viewLifecycleOwner.lifecycleScope.launch {
            val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            val product = currentProduct?.copy(
                name = name,
                unit = unit,
                price = price,
                notes = notes,
                imagePath = selectedImagePath ?: currentProduct?.imagePath,
                updatedAt = now
            ) ?: Product(
                name = name,
                unit = unit,
                price = price,
                stockQty = 0.0,
                createdAt = now,
                notes = notes,
                imagePath = selectedImagePath
            )

            val recipeList = ingredientAdapter.getItems().map { it.recipe.copy(productId = product.id) }
            val presentations = presentationAdapter.getItems()
            val variants = variantAdapter.getItems()

            productViewModel.saveProduct(product, recipeList, presentations, variants)
                .observe(viewLifecycleOwner) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), "Producto guardado", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    } else {
                        Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}