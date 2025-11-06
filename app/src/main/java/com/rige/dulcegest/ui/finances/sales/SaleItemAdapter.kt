package com.rige.dulcegest.ui.finances.sales

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.core.utils.toSoles
import com.rige.dulcegest.data.local.entities.relations.SaleItemWithProduct
import com.rige.dulcegest.databinding.ItemSaleItemBinding

class SaleItemAdapter(
    private val onQuantityChange: (item: SaleItemWithProduct, newQty: Double) -> Unit
) : ListAdapter<SaleItemWithProduct, SaleItemAdapter.SaleItemViewHolder>(DiffCallback()) {

    inner class SaleItemViewHolder(
        private val binding: ItemSaleItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SaleItemWithProduct) {
            val currentQty = item.item.qty
            val unitPrice = item.item.unitPrice
            val lineTotal = item.item.lineTotal

            binding.tvItemName.text = item.product.name
            binding.tvItemDetails.text = "${unitPrice.toSoles()}/u | Total: ${lineTotal.toSoles()}"
            binding.tvQuantity.text = currentQty.toString()

            binding.btnIncrement.setOnClickListener {
                val newQty = currentQty + 1.0
                onQuantityChange(item, newQty)
            }

            binding.btnDecrement.setOnClickListener {
                val newQty = currentQty - 1.0
                if (newQty <= 0) {
                    onQuantityChange(item, 0.0)
                } else {
                    onQuantityChange(item, newQty)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleItemViewHolder {
        val binding = ItemSaleItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SaleItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaleItemViewHolder, position: Int) =
        holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<SaleItemWithProduct>() {
        override fun areItemsTheSame(oldItem: SaleItemWithProduct, newItem: SaleItemWithProduct) =
            oldItem.item.productId == newItem.item.productId && oldItem.item.presentationId == newItem.item.presentationId // ID único para producto/presentación en la lista

        override fun areContentsTheSame(oldItem: SaleItemWithProduct, newItem: SaleItemWithProduct) =
            oldItem == newItem
    }
}