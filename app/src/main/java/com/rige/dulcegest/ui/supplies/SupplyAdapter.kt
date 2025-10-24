package com.rige.dulcegest.ui.supplies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.R
import com.rige.dulcegest.data.db.entities.Supply

class SupplyAdapter(
    private val onClick: (Supply) -> Unit
) : ListAdapter<Supply, SupplyAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val name = view.findViewById<TextView>(R.id.tvName)
        private val info = view.findViewById<TextView>(R.id.tvInfo)

        fun bind(item: Supply) {
            name.text = item.name
            info.text = "${item.unit} • Stock: ${item.stockQty}"
            view.setOnClickListener { onClick(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Supply>() {
        override fun areItemsTheSame(old: Supply, new: Supply) = old.id == new.id
        override fun areContentsTheSame(old: Supply, new: Supply) = old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_supply, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))
}