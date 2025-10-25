package com.rige.dulcegest.ui.finances.sales

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.R
import com.rige.dulcegest.data.local.entities.relations.SaleItemWithProduct

class SaleItemAdapter(
    private val onRemove: (SaleItemWithProduct) -> Unit
) : ListAdapter<SaleItemWithProduct, SaleItemAdapter.SaleItemViewHolder>(DiffCallback()) {

    inner class SaleItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvItemName)
        private val tvDetails: TextView = itemView.findViewById(R.id.tvItemDetails)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)

        fun bind(item: SaleItemWithProduct) {
            tvName.text = item.product.name
            tvDetails.text = "Cant: ${item.item.qty} | $${item.item.unitPrice} | Total: $${item.item.lineTotal}"
            btnRemove.setOnClickListener { onRemove(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SaleItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_sale_item, parent, false)
        )

    override fun onBindViewHolder(holder: SaleItemViewHolder, position: Int) =
        holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<SaleItemWithProduct>() {
        override fun areItemsTheSame(oldItem: SaleItemWithProduct, newItem: SaleItemWithProduct) =
            oldItem.item.id == newItem.item.id

        override fun areContentsTheSame(oldItem: SaleItemWithProduct, newItem: SaleItemWithProduct) =
            oldItem == newItem
    }
}