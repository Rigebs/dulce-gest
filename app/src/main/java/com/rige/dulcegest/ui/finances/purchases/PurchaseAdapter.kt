package com.rige.dulcegest.ui.finances.purchases

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.core.utils.toFriendlyDateTime
import com.rige.dulcegest.core.utils.toSoles
import com.rige.dulcegest.data.local.entities.Purchase
import com.rige.dulcegest.databinding.ItemPurchaseBinding

class PurchaseAdapter(
private val onClick: (Purchase) -> Unit
) : ListAdapter<Purchase, PurchaseAdapter.PurchaseViewHolder>(DiffCallback()) {

    inner class PurchaseViewHolder(
        private val binding: ItemPurchaseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Purchase) {
            with(binding) {
                tvSupplierName.text = item.supplier ?: "General"
                tvTotalAmount.text = "Total: ${item.totalPrice.toSoles()}"
                tvDate.text = "Fecha: ${item.date?.toFriendlyDateTime() ?: "--/--/----"}"

                root.setOnClickListener { onClick(item) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseViewHolder {
        val binding = ItemPurchaseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PurchaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PurchaseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Purchase>() {
        override fun areItemsTheSame(
            oldItem: Purchase,
            newItem: Purchase
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Purchase,
            newItem: Purchase
        ) = oldItem == newItem
    }
}
