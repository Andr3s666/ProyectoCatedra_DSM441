package com.example.distribupan

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.distribupan.databinding.ItemProductCardBinding // Usamos el binding del ítem
import java.util.Locale // Para formatear el precio


class ProductAdapter(
    private val products: List<Product>,
    private val onBuyClicked: (Product) -> Unit // Función lambda para el botón Comprar
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // 1. Define la clase ViewHolder
    inner class ProductViewHolder(private val binding: ItemProductCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Función que enlaza los datos de un Producto con la vista
        fun bind(product: Product) {
            binding.tvProductName.text = product.name

            // Formatea el precio a moneda
            binding.tvProductPrice.text = String.format(Locale.US, "$%.2f", product.price)

            // Asigna la imagen
            binding.ivProductImage.setImageResource(product.imageResId)

            // Opcional: añade la descripción
            binding.tvDescription.text = "Insumo de panadería."

            // Botón para agregar al carrito
            binding.btnBuy.setOnClickListener {
                CartManager.addToCart(product)
                Toast.makeText(binding.root.context, "${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
            }


            // Manejo del evento al presionar toda la tarjeta (para ver detalles)
            binding.root.setOnClickListener {
                // Puedes cambiar esto para iniciar la Activity de Detalle
                Toast.makeText(binding.root.context, "Ver detalles de ${product.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 2. Crea nuevos ViewHolders (infla el layout del ítem)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    // 3. Devuelve el tamaño total de la lista
    override fun getItemCount(): Int = products.size

    // 4. Reemplaza el contenido de una vista (llama a la función bind)
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }
}