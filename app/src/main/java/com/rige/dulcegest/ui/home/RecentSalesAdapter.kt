package com.rige.dulcegest.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.core.utils.toFriendlyDateTime
import com.rige.dulcegest.data.local.entities.relations.SaleWithItems
import com.rige.dulcegest.databinding.ItemRecentSaleBinding

class RecentSalesAdapter(
    private val sales: List<SaleWithItems>
) : RecyclerView.Adapter<RecentSalesAdapter.SaleViewHolder>() {

    inner class SaleViewHolder(val binding: ItemRecentSaleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val binding = ItemRecentSaleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SaleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val saleWithItems = sales[position]
        val binding = holder.binding

        binding.txtCustomer.text = if (saleWithItems.sale.customer.isNullOrBlank()) "Cliente genérico" else saleWithItems.sale.customer

        binding.txtAmount.text = "S/ %.2f".format(saleWithItems.sale.totalAmount)
        binding.txtDate.text = saleWithItems.sale.saleDate?.toFriendlyDateTime() ?: "--/--/----"
    }

    override fun getItemCount(): Int = sales.size
}