package com.rige.dulcegest.ui.products.form

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.R
import com.rige.dulcegest.core.utils.toSoles
import com.rige.dulcegest.data.local.entities.ProductVariant

class ProductVariantAdapter(
    private val onRemove: (ProductVariant) -> Unit
) : ListAdapter<ProductVariant, ProductVariantAdapter.VariantViewHolder>(DiffCallback()) {

    inner class VariantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name = view.findViewById<TextView>(R.id.tvVariantName)
        private val price = view.findViewById<TextView>(R.id.tvVariantPrice)
        private val removeBtn = view.findViewById<ImageButton>(R.id.btnRemoveVariant)

        fun bind(variant: ProductVariant) {
            name.text = variant.name
            price.text = variant.price.toSoles()
            removeBtn.setOnClickListener { onRemove(variant) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_variant, parent, false)
        return VariantViewHolder(view)
    }

    override fun onBindViewHolder(holder: VariantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<ProductVariant>() {
        override fun areItemsTheSame(oldItem: ProductVariant, newItem: ProductVariant): Boolean {
            return oldItem.id == newItem.id && oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: ProductVariant, newItem: ProductVariant): Boolean {
            return oldItem == newItem
        }
    }

    fun addVariant(variant: ProductVariant, onListUpdated: () -> Unit) {
        val updatedList = currentList.toMutableList().apply { add(variant.copy()) }

        submitList(updatedList) {
            onListUpdated()
        }
    }

    fun removeVariant(variant: ProductVariant, onListUpdated: () -> Unit) {
        val updated = currentList.toMutableList().apply { remove(variant) }

        submitList(updated) {
            onListUpdated()
        }
    }

    fun setItems(list: List<ProductVariant>) {
        submitList(list)
    }

    fun getItems(): List<ProductVariant> = currentList
}