package com.rige.dulcegest.ui.products.form

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.data.local.entities.ProductPresentation
import com.rige.dulcegest.databinding.ItemProductPresentationBinding

class ProductPresentationAdapter(
    private val onRemove: (ProductPresentation) -> Unit
) : ListAdapter<ProductPresentation, ProductPresentationAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemProductPresentationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductPresentation) {
            binding.txtPresentationName.text = item.name
            binding.txtPresentationQuantity.text = "${item.quantity} ${item.unit}"
            binding.txtPresentationPrice.text = "S/. ${item.price}"
            binding.btnRemove.setOnClickListener { onRemove(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductPresentationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<ProductPresentation>() {
        override fun areItemsTheSame(
            oldItem: ProductPresentation,
            newItem: ProductPresentation
        ): Boolean {
            return oldItem.id == newItem.id && oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: ProductPresentation,
            newItem: ProductPresentation
        ): Boolean = oldItem == newItem
    }

    fun addPresentation(presentation: ProductPresentation, onListUpdated: () -> Unit) {
        val updatedList = currentList.toMutableList().apply { add(presentation.copy()) }

        submitList(updatedList.toList()) {
            onListUpdated()
        }
    }

    fun removePresentation(presentation: ProductPresentation, onListUpdated: () -> Unit) {
        val updatedList = currentList.toMutableList().apply { remove(presentation) }

        submitList(updatedList) {
            onListUpdated()
        }
    }

    fun setItems(newItems: List<ProductPresentation>) {
        submitList(newItems)
    }

    fun getItems(): List<ProductPresentation> = currentList
}