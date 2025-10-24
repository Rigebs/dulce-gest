package com.rige.dulcegest.ui.purchases

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.rige.dulcegest.R
import com.rige.dulcegest.data.db.entities.Supply

class SupplySpinnerAdapter(
    context: Context,
    private val supplies: List<Supply>
) : ArrayAdapter<Supply>(context, 0, supplies) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // 🔹 Este es el item SELECCIONADO → solo nombre
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_supply_spinner_selected, parent, false)

        val supply = supplies[position]
        val tvName = view.findViewById<TextView>(R.id.tvNameSelected)
        val tvConversion = view.findViewById<TextView>(R.id.tvConversionSelected)

        tvName.text = supply.name
        // ❌ No queremos mostrar la conversión cuando está seleccionado
        tvConversion.visibility = View.GONE

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        // 🔹 Este es el item en la LISTA desplegable → nombre + conversión si existe
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_supply_spinner, parent, false)

        val supply = supplies[position]
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvConversion = view.findViewById<TextView>(R.id.tvConversion)

        tvName.text = supply.name

        if (!supply.purchaseUnit.isNullOrBlank() && supply.conversionFactor != null) {
            tvConversion.visibility = View.VISIBLE
            tvConversion.text =
                "(${supply.purchaseUnit} = ${supply.conversionFactor} ${supply.unit})"
        } else {
            tvConversion.visibility = View.GONE
        }

        return view
    }
}