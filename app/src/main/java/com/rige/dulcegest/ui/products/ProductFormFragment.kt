package com.rige.dulcegest.ui.products

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.data.db.entities.Ingredient
import com.rige.dulcegest.data.db.entities.Product
import com.rige.dulcegest.data.db.entities.ProductRecipe
import com.rige.dulcegest.data.db.relations.ProductRecipeWithIngredient
import com.rige.dulcegest.databinding.FragmentProductFormBinding
import com.rige.dulcegest.ui.viewmodels.IngredientViewModel
import com.rige.dulcegest.ui.viewmodels.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

@AndroidEntryPoint
class ProductFormFragment : Fragment() {

    private var _binding: FragmentProductFormBinding? = null
    private val binding get() = _binding!!

    private val productViewModel: ProductViewModel by viewModels()
    private val ingredientViewModel: IngredientViewModel by viewModels()

    private var productId: Long? = null
    private lateinit var adapter: RecipeIngredientAdapter
    private var ingredientList: List<Ingredient> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        productId = arguments?.getLong("productId")

        adapter = RecipeIngredientAdapter(onRemove = { adapter.removeIngredient(it) })

        binding.recyclerIngredients.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerIngredients.adapter = adapter

        // Observa todos los ingredientes disponibles
        ingredientViewModel.ingredients.observe(viewLifecycleOwner) {
            ingredientList = it
        }

        // Si es edición, carga producto y su receta
        productId?.let { id ->
            productViewModel.getProductById(id).observe(viewLifecycleOwner) { product ->
                product?.let {
                    binding.inputName.setText(it.name)
                    binding.inputUnit.setText(it.unit)
                    binding.inputPrice.setText(it.price.toString())
                    binding.inputNotes.setText(it.notes ?: "")
                }
            }

            productViewModel.getRecipeWithIngredients(id).observe(viewLifecycleOwner) { recipeWithIngredients ->
                adapter.setItems(recipeWithIngredients)
            }
        }

        binding.btnAddIngredient.setOnClickListener { showIngredientSelector() }
        binding.btnSave.setOnClickListener { saveProduct() }
    }

    /**
     * Muestra un diálogo para seleccionar un ingrediente de la lista
     */
    private fun showIngredientSelector() {
        if (ingredientList.isEmpty()) {
            Toast.makeText(requireContext(), "No hay ingredientes disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val ingredientNames = ingredientList.map { it.name }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona un ingrediente")
            .setItems(ingredientNames) { _, which ->
                val selectedIngredient = ingredientList[which]

                // Construimos la relación para el adapter
                val recipeItem = ProductRecipeWithIngredient(
                    recipe = ProductRecipe(
                        id = 0,
                        productId = productId ?: 0,
                        ingredientId = selectedIngredient.id,
                        qtyPerUnit = 0.0
                    ),
                    ingredient = selectedIngredient
                )

                adapter.addIngredient(recipeItem)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Guarda el producto con su receta
     */
    private fun saveProduct() {
        val name = binding.inputName.text.toString().trim()
        val unit = binding.inputUnit.text.toString().trim()
        val price = binding.inputPrice.text.toString().toDoubleOrNull() ?: 0.0
        val notes = binding.inputNotes.text.toString().trim().ifEmpty { null }

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        val product = Product(
            id = productId ?: 0,
            name = name,
            unit = unit.ifEmpty { "u" },
            price = price,
            stockQty = 0.0,
            createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            notes = notes
        )

        val recipeList = adapter.getItems().map {
            it.recipe.copy(productId = productId ?: 0)
        }

        println(recipeList)

        productViewModel.saveProduct(product, recipeList).observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Producto guardado", Toast.LENGTH_SHORT).show()
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