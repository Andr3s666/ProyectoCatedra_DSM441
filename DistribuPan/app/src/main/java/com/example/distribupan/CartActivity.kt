package com.example.distribupan

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.distribupan.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adapter: trabajamos con IDs Int
        adapter = CartAdapter(
            onIncrease = { id: Int ->
                CartManager.increase(id)
                refreshUI()
            },
            onDecrease = { id: Int ->
                CartManager.decrease(id)
                refreshUI()
            }
        )

        binding.recyclerViewCart.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCart.adapter = adapter

        refreshUI()

        // Finalizar compra -> generar PDF en Descargas
        binding.btnCheckout.setOnClickListener {
            val items = CartManager.getItems()
            if (items.isEmpty()) {
                Toast.makeText(this, "Tu carrito está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uri = InvoicePdfGenerator.generate(this, items)
            if (uri != null) {
                CartManager.clear()
                refreshUI()
                Toast.makeText(this, "Factura guardada en Descargas", Toast.LENGTH_LONG).show()

                // Si quieres abrirlo al instante, descomenta:
                /*
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(intent, "Abrir factura"))
                */
            } else {
                Toast.makeText(this, "No se pudo generar la factura", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun refreshUI() {
        adapter.submitList(CartManager.getItems().toList())
        binding.tvTotal.text = "Total: $${String.format("%.2f", CartManager.total())}"
        binding.btnCheckout.isEnabled = CartManager.getItems().isNotEmpty()
    }
}



