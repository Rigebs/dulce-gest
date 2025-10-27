package com.rige.dulcegest.ui.products.productions

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.R
import com.rige.dulcegest.core.utils.toFriendlyDateTime
import com.rige.dulcegest.core.utils.toSoles
import com.rige.dulcegest.data.local.entities.relations.ProductionBatchWithProductAndConsumptions
import com.rige.dulcegest.databinding.ItemProductionBinding

class ProductionAdapter(
    private val onEdit: (ProductionBatchWithProductAndConsumptions) -> Unit,
    private val onDelete: (ProductionBatchWithProductAndConsumptions) -> Unit
) : ListAdapter<ProductionBatchWithProductAndConsumptions, ProductionAdapter.ProductionViewHolder>(DiffCallback()) {

    inner class ProductionViewHolder(
        private val binding: ItemProductionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductionBatchWithProductAndConsumptions) = with(binding) {
            tvProductName.text = item.product.name
            tvQuantity.text = "Cantidad: ${item.batch.quantityProduced}"
            tvTotalCost.text = "Costo aprox.: ${item.batch.totalCost.toSoles()}"
            tvDate.text = "Fecha: ${item.batch.date?.toFriendlyDateTime() ?: "--/--/----"}"

            root.setOnClickListener {
                val popup = PopupMenu(root.context, root)
                popup.menuInflater.inflate(R.menu.menu_production_item, popup.menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_edit -> { onEdit(item); true }
                        R.id.action_delete -> { onDelete(item); true }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductionViewHolder {
        val binding = ItemProductionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<ProductionBatchWithProductAndConsumptions>() {
        override fun areItemsTheSame(oldItem: ProductionBatchWithProductAndConsumptions, newItem: ProductionBatchWithProductAndConsumptions) =
            oldItem.batch.id == newItem.batch.id

        override fun areContentsTheSame(oldItem: ProductionBatchWithProductAndConsumptions, newItem: ProductionBatchWithProductAndConsumptions) =
            oldItem == newItem
    }
}