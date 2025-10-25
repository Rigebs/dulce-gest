package com.rige.dulcegest.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.R
import com.rige.dulcegest.data.local.entities.Sale

class RecentSalesAdapter(
    private val sales: List<Sale>
) : RecyclerView.Adapter<RecentSalesAdapter.SaleViewHolder>() {

    inner class SaleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtCustomer: TextView = itemView.findViewById(R.id.txtCustomer)
        val txtAmount: TextView = itemView.findViewById(R.id.txtAmount)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_sale, parent, false)
        return SaleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val sale = sales[position]
        holder.txtCustomer.text = sale.customer ?: "Cliente genérico"
        holder.txtAmount.text = "S/ %.2f".format(sale.totalAmount)
        holder.txtDate.text = sale.saleDate ?: ""
    }

    override fun getItemCount(): Int = sales.size
}
