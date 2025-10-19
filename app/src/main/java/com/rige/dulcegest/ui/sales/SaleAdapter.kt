package com.rige.dulcegest.ui.sales

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.R
import com.rige.dulcegest.data.db.entities.Sale

class SaleAdapter(
    private val onClick: (Sale) -> Unit
) : ListAdapter<Sale, SaleAdapter.SaleViewHolder>(DiffCallback()) {

    inner class SaleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvSaleDate)
        private val tvCustomer: TextView = itemView.findViewById(R.id.tvCustomer)
        private val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)

        fun bind(sale: Sale) {
            tvDate.text = sale.saleDate?.substring(0, 10) ?: "--/--/----"
            tvCustomer.text = sale.customer ?: "Sin cliente"
            tvTotal.text = "Total: $${"%.2f".format(sale.totalAmount)}"
            itemView.setOnClickListener { onClick(sale) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SaleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false))

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) =
        holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<Sale>() {
        override fun areItemsTheSame(oldItem: Sale, newItem: Sale) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Sale, newItem: Sale) = oldItem == newItem
    }
}
