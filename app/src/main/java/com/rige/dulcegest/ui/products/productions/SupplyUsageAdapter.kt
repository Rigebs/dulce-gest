package com.rige.dulcegest.ui.products.productions

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.data.local.entities.relations.ProductRecipeWithSupply
import com.rige.dulcegest.databinding.ItemSupplyUsageBinding

class SupplyUsageAdapter(
    private val supplies: List<ProductRecipeWithSupply>
) : RecyclerView.Adapter<SupplyUsageAdapter.ViewHolder>() {

    private val usedQuantities = mutableMapOf<Long, Double>()

    inner class ViewHolder(val binding: ItemSupplyUsageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSupplyUsageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = supplies[position]
        with(holder.binding) {
            tvSupplyName.text = item.supply.name
            tvUnit.text = item.supply.unit
            inputQtyUsed.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    usedQuantities[item.supply.id] = s?.toString()?.toDoubleOrNull() ?: 0.0
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    override fun getItemCount(): Int = supplies.size

    fun getQuantities(): Map<Long, Double> = usedQuantities
}