package com.rige.dulcegest.ui.finances.sales

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.core.utils.toFriendlyDateTime
import com.rige.dulcegest.core.utils.toSoles
import com.rige.dulcegest.data.local.entities.Sale
import com.rige.dulcegest.databinding.ItemSaleBinding

class SaleAdapter(
    private val onClick: (Sale) -> Unit
) : ListAdapter<Sale, SaleAdapter.SaleViewHolder>(DiffCallback()) {

    inner class SaleViewHolder(val binding: ItemSaleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sale: Sale) {
            with(binding) {
                tvSaleDate.text = sale.saleDate?.toFriendlyDateTime() ?: "--/--/----"
                tvCustomer.text = if (sale.customer.isNullOrBlank()) "Cliente genérico" else sale.customer
                tvTotal.text = "Total: ${sale.totalAmount.toSoles()}"
                root.setOnClickListener { onClick(sale) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val binding = ItemSaleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SaleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Sale>() {
        override fun areItemsTheSame(oldItem: Sale, newItem: Sale) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Sale, newItem: Sale) = oldItem == newItem
    }
}