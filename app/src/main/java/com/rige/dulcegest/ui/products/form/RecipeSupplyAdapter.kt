package com.rige.dulcegest.ui.products.form

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.data.local.entities.relations.ProductRecipeWithSupply
import com.rige.dulcegest.databinding.ItemRecipeSupplyBinding

class RecipeSupplyAdapter(
    private val onRemove: (ProductRecipeWithSupply) -> Unit
) : ListAdapter<ProductRecipeWithSupply, RecipeSupplyAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemRecipeSupplyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductRecipeWithSupply) {
            binding.txtSupplyName.text = item.supply.name

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