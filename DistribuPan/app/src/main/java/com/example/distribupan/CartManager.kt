package com.example.distribupan

data class CartItem(
    val product: Product,
    var quantity: Int = 1
)

object CartManager {
    private val cartItems = mutableListOf<CartItem>()

    fun addToCart(product: Product) {
        val existing = cartItems.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity++
        } else {
            cartItems.add(CartItem(product))
        }
    }

    fun getItems(): List<CartItem> = cartItems

    fun increase(productId: Int) {
        cartItems.find { it.product.id == productId }?.quantity?.let { it + 1 }?.also {
            cartItems.find { it.product.id == productId }!!.quantity = it
        }
    }

    fun decrease(productId: Int) {
        val item = cartItems.find { it.product.id == productId }
        if (item != null) {
            item.quantity--
            if (item.quantity <= 0) cartItems.remove(item)
        }
    }

    fun clear() = cartItems.clear()

    fun total(): Double = cartItems.sumOf { it.product.price * it.quantity }
    fun increase(productId: String) {}
    fun decrease(productId: String) {}
}
