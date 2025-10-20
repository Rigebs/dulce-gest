package com.rige.dulcegest.ui.ingredients

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.R
import com.rige.dulcegest.data.db.entities.Ingredient

class IngredientAdapter(
    private val onClick: (Ingredient) -> Unit
) : ListAdapter<Ingredient, IngredientAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val name = view.findViewById<TextView>(R.id.tvName)
        private val info = view.findViewById<TextView>(R.id.tvInfo)

        fun bind(item: Ingredient) {
            name.text = item.name
            info.text = "${item.unit} • Stock: ${item.stockQty}"
            view.setOnClickListener { onClick(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Ingredient>() {
        override fun areItemsTheSame(old: Ingredient, new: Ingredient) = old.id == new.id
        override fun areContentsTheSame(old: Ingredient, new: Ingredient) = old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))
}