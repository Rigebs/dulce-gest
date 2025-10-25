package com.rige.dulcegest.ui.products.production

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.R
import com.rige.dulcegest.data.local.entities.relations.ProductionBatchWithProduct

class ProductionAdapter(
    private val onClick: (ProductionBatchWithProduct) -> Unit
) : ListAdapter<ProductionBatchWithProduct, ProductionAdapter.ProductionViewHolder>(
    DiffCallback()
) {

    inner class ProductionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        private val tvTotalCost: TextView = itemView.findViewById(R.id.tvTotalCost)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(item: ProductionBatchWithProduct) {
            tvProductName.text = item.product.name
            tvQuantity.text = "Cantidad: ${item.batch.quantityProduced}"
            tvTotalCost.text = "Costo: $${item.batch.totalCost}"
            tvDate.text = "Fecha: ${item.batch.date?.substring(0,10) ?: "--/--/----"}"

            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ProductionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_production, parent, false)
        )

    override fun onBindViewHolder(holder: ProductionViewHolder, position: Int) =
        holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<ProductionBatchWithProduct>() {
        override fun areItemsTheSame(
            oldItem: ProductionBatchWithProduct,
            newItem: ProductionBatchWithProduct
        ) = oldItem.batch.id == newItem.batch.id

        override fun areContentsTheSame(
            oldItem: ProductionBatchWithProduct,
            newItem: ProductionBatchWithProduct
        ) = oldItem == newItem
    }
}