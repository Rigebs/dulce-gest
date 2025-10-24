package com.rige.dulcegest.ui.products

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.data.db.relations.ProductRecipeWithSupply
import com.rige.dulcegest.databinding.ItemRecipeSupplyBinding

class RecipeSupplyAdapter(
    private val onRemove: (ProductRecipeWithSupply) -> Unit
) : ListAdapter<ProductRecipeWithSupply, RecipeSupplyAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemRecipeSupplyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var watcher: TextWatcher? = null

        fun bind(item: ProductRecipeWithSupply) {
            binding.txtSupplyName.text = item.supply.name

            watcher?.let { binding.inputQty.removeTextChangedListener(it) }

            binding.inputQty.setText(item.recipe.qtyPerUnit.toString())

            // --- 🎯 Limpieza de "0.0" al enfocar y restaurar ---
            binding.inputQty.setOnFocusChangeListener { v, hasFocus ->
                val editText = binding.inputQty
                if (hasFocus) {
                    val txt = editText.text.toString().trim()
                    if (txt == "0" || txt == "0.0") {
                        editText.text.clear()
                    }
                } else {
                    if (editText.text.isNullOrBlank()) {
                        editText.setText("0.0")
                    }
                }
            }

            watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val newQty = s.toString().toDoubleOrNull() ?: 0.0

                    val currentItem = getItem(adapterPosition)

                    if (newQty != currentItem.recipe.qtyPerUnit) {

                        val updatedList = currentList.toMutableList()

                        val index = updatedList.indexOfFirst {
                            it.recipe.supplyId == currentItem.recipe.supplyId
                        }

                        if (index != -1) {
                            val updatedItem = updatedList[index].copy(
                                recipe = updatedList[index].recipe.copy(qtyPerUnit = newQty)
                            )
                            updatedList[index] = updatedItem

                            submitList(updatedList)
                        }
                    }
                }
            }

            binding.inputQty.addTextChangedListener(watcher)

            binding.btnRemove.setOnClickListener {
                onRemove(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeSupplyBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<ProductRecipeWithSupply>() {
        override fun areItemsTheSame(
            oldItem: ProductRecipeWithSupply,
            newItem: ProductRecipeWithSupply
        ): Boolean {
            return oldItem.recipe.supplyId == newItem.recipe.supplyId
        }

        override fun areContentsTheSame(
            oldItem: ProductRecipeWithSupply,
            newItem: ProductRecipeWithSupply
        ): Boolean = oldItem == newItem
    }

    fun addSupply(recipe: ProductRecipeWithSupply, onListUpdated: () -> Unit) {
        val updatedList = currentList.toMutableList().apply { add(0, recipe.copy()) }
        submitList(updatedList.toList(), onListUpdated)
    }

    fun removeSupply(recipe: ProductRecipeWithSupply, onListUpdated: () -> Unit) {
        val updatedList = currentList.toMutableList().apply { remove(recipe) }
        submitList(updatedList.toList(), onListUpdated)
    }

    fun setItems(newItems: List<ProductRecipeWithSupply>) {
        submitList(newItems)
    }

    fun getItems(): List<ProductRecipeWithSupply> = currentList
}