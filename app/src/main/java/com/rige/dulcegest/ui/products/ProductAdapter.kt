package com.rige.dulcegest.ui.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.rige.dulcegest.R
import com.rige.dulcegest.data.db.entities.Product
import com.rige.dulcegest.databinding.ItemProductBinding
import androidx.core.net.toUri
import coil.transform.RoundedCornersTransformation

class ProductAdapter(
    private val onClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.txtProductName.text = product.name
            binding.txtProductInfo.text = "U.M.: ${product.unit}  |  Precio: $${product.price}"
            binding.txtStock.text = "Stock: ${product.stockQty}"

            if (!product.imagePath.isNullOrEmpty()) {
                binding.imgProduct.load(product.imagePath.toUri()) {
                    placeholder(R.drawable.ic_placeholder_image)
                    error(R.drawable.ic_placeholder_image)
                    transformations(RoundedCornersTransformation(12f))
                }
            } else {
                binding.imgProduct.setImageResource(R.drawable.ic_placeholder_image)
            }

            binding.root.setOnClickListener {
                onClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
    }
}