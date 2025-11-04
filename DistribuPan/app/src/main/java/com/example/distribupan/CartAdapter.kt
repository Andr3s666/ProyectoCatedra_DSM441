package com.example.distribupan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.distribupan.databinding.ItemCartCardBinding

class CartAdapter(
    private val onIncrease: (Int) -> Unit,
    private val onDecrease: (Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val items = mutableListOf<CartItem>()

    fun submitList(list: List<CartItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class CartViewHolder(val binding: ItemCartCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            runCatching { binding.ivThumb.setImageResource(item.product.imageResId) }
            binding.tvName.text = item.product.name
            binding.tvQuantity.text = item.quantity.toString()
            binding.tvPrice.text = "$${String.format("%.2f", item.product.price)}"

            // product.id es Int → pasamos Int
            binding.btnPlus.setOnClickListener { onIncrease(item.product.id) }
            binding.btnMinus.setOnClickListener { onDecrease(item.product.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}



