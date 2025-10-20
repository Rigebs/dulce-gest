package com.rige.dulcegest.ui.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.data.db.entities.ProductPresentation
import com.rige.dulcegest.databinding.ItemProductPresentationBinding

class ProductPresentationAdapter(
    private val onRemove: (ProductPresentation) -> Unit
) : ListAdapter<ProductPresentation, ProductPresentationAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemProductPresentationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductPresentation) {
            binding.txtPresentationName.text = item.name
            binding.txtPresentationPrice.text = "S/ ${item.price}"
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
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ProductPresentation,
            newItem: ProductPresentation
        ): Boolean = oldItem == newItem
    }

    fun addPresentation(presentation: ProductPresentation) {
        val updatedList = currentList.toMutableList().apply { add(presentation) }
        submitList(updatedList)
    }

    fun removePresentation(presentation: ProductPresentation) {
        val updatedList = currentList.toMutableList().apply { remove(presentation) }
        submitList(updatedList)
    }

    fun setItems(newItems: List<ProductPresentation>) {
        submitList(newItems)
    }

    fun getItems(): List<ProductPresentation> = currentList
}