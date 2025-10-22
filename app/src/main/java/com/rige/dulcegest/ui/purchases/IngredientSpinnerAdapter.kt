package com.rige.dulcegest.ui.purchases

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.rige.dulcegest.R
import com.rige.dulcegest.data.db.entities.Ingredient

class IngredientSpinnerAdapter(
    context: Context,
    private val ingredients: List<Ingredient>
) : ArrayAdapter<Ingredient>(context, 0, ingredients) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // 🔹 Este es el item SELECCIONADO → solo nombre
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_ingredient_spinner_selected, parent, false)

        val ingredient = ingredients[position]
        val tvName = view.findViewById<TextView>(R.id.tvNameSelected)
        val tvConversion = view.findViewById<TextView>(R.id.tvConversionSelected)

        tvName.text = ingredient.name
        // ❌ No queremos mostrar la conversión cuando está seleccionado
        tvConversion.visibility = View.GONE

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        // 🔹 Este es el item en la LISTA desplegable → nombre + conversión si existe
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_ingredient_spinner, parent, false)

        val ingredient = ingredients[position]
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvConversion = view.findViewById<TextView>(R.id.tvConversion)

        tvName.text = ingredient.name

        if (!ingredient.purchaseUnit.isNullOrBlank() && ingredient.conversionFactor != null) {
            tvConversion.visibility = View.VISIBLE
            tvConversion.text =
                "(${ingredient.purchaseUnit} = ${ingredient.conversionFactor} ${ingredient.unit})"
        } else {
            tvConversion.visibility = View.GONE
        }

        return view
    }
}