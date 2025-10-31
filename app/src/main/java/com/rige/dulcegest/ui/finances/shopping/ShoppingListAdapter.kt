package com.rige.dulcegest.ui.finances.shopping

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.data.local.entities.relations.ShoppingListItemWithSupply
import com.rige.dulcegest.databinding.ItemShoppingListBinding

class ShoppingListAdapter(
    private val onItemClick: (ShoppingListItemWithSupply) -> Unit,
    private val onDelete: (ShoppingListItemWithSupply) -> Unit
) : ListAdapter<ShoppingListItemWithSupply, ShoppingListAdapter.ViewHolder>(

    object : DiffUtil.ItemCallback<ShoppingListItemWithSupply>() {
        override fun areItemsTheSame(oldItem: ShoppingListItemWithSupply, newItem: ShoppingListItemWithSupply) =
            oldItem.item.id == newItem.item.id

        override fun areContentsTheSame(oldItem: ShoppingListItemWithSupply, newItem: ShoppingListItemWithSupply) =
            oldItem == newItem
    }
) {
    inner class ViewHolder(val binding: ItemShoppingListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ShoppingListItemWithSupply) {
            with(binding) {
                txtName.text = data.supply.name
                txtQuantity.text = "${data.item.quantity} ${data.item.unit}"

                root.setOnClickListener {
                    onItemClick(data)
                }

                btnDelete.setOnClickListener {
                    onDelete(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShoppingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}