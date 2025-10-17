package com.rige.dulcegest.ui.products

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.data.db.relations.ProductRecipeWithIngredient
import com.rige.dulcegest.databinding.ItemRecipeIngredientBinding

class RecipeIngredientAdapter(
    private val onRemove: (ProductRecipeWithIngredient) -> Unit
) : RecyclerView.Adapter<RecipeIngredientAdapter.ViewHolder>() {

    private val items = mutableListOf<ProductRecipeWithIngredient>()

    inner class ViewHolder(private val binding: ItemRecipeIngredientBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var watcher: TextWatcher? = null

        fun bind(item: ProductRecipeWithIngredient) {
            binding.txtIngredientName.text = item.ingredient.name

            // Evitar acumulación de watchers
            watcher?.let { binding.inputQty.removeTextChangedListener(it) }

            // Mostrar cantidad actual
            binding.inputQty.setText(item.recipe.qtyPerUnit.toString())

            // --- 🎯 Limpieza de "0.0" al enfocar ---
            binding.inputQty.setOnFocusChangeListener { v, hasFocus ->
                val editText = binding.inputQty
                if (hasFocus) {
                    // Si tiene 0 o 0.0 al enfocar, limpiamos
                    val txt = editText.text.toString().trim()
                    if (txt == "0" || txt == "0.0") {
                        editText.text.clear()
                    }
                } else {
                    // Si pierde foco y queda vacío, restauramos 0.0
                    if (editText.text.isNullOrBlank()) {
                        editText.setText("0.0")
                    }
                }
            }

            // --- Actualización del valor en la lista ---
            watcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val newQty = s.toString().toDoubleOrNull() ?: 0.0
                    val index = items.indexOfFirst { it.recipe.ingredientId == item.recipe.ingredientId }
                    if (index != -1) {
                        items[index] = items[index].copy(
                            recipe = items[index].recipe.copy(qtyPerUnit = newQty)
                        )
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }

            binding.inputQty.addTextChangedListener(watcher)

            // --- Eliminar ingrediente ---
            binding.btnRemove.setOnClickListener {
                onRemove(item)
                removeIngredient(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeIngredientBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addIngredient(recipe: ProductRecipeWithIngredient) {
        items.add(0, recipe)
        notifyItemInserted(0)
    }

    fun removeIngredient(recipe: ProductRecipeWithIngredient) {
        val index = items.indexOfFirst { it.recipe.ingredientId == recipe.recipe.ingredientId }
        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun setItems(list: List<ProductRecipeWithIngredient>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun getItems(): List<ProductRecipeWithIngredient> = items.toList()
}