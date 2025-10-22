package com.rige.dulcegest.ui.products

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.R
import com.rige.dulcegest.data.db.relations.ProductRecipeWithIngredient

class IngredientUsageAdapter(
    private val ingredients: List<ProductRecipeWithIngredient>
) : RecyclerView.Adapter<IngredientUsageAdapter.ViewHolder>() {

    private val usedQuantities = mutableMapOf<Long, Double>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.tvIngredientName)
        val unit = view.findViewById<TextView>(R.id.tvUnit)
        val inputQty = view.findViewById<EditText>(R.id.inputQtyUsed)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient_usage, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ingredients[position]
        holder.name.text = item.ingredient.name
        holder.unit.text = item.ingredient.unit

        holder.inputQty.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                usedQuantities[item.ingredient.id] = s?.toString()?.toDoubleOrNull() ?: 0.0
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun getItemCount(): Int = ingredients.size

    fun getQuantities(): Map<Long, Double> = usedQuantities
}
