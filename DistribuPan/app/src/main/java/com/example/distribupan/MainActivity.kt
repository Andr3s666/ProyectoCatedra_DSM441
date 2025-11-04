package com.example.distribupan

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.distribupan.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // --- 1. CONFIGURACIÓN DE LA BARRA SUPERIOR Y EL MENÚ LATERAL (DRAWER) ---
        setupDrawerAndToolbar()

        // --- 2. MOSTRAR EL NOMBRE DEL USUARIO ---
        displayGreeting()

        // --- 3. CONFIGURACIÓN DEL CATÁLOGO DE PRODUCTOS (RecyclerView) ---
        setupProductCatalog()
    }

    // Método para configurar el ActionBar y el DrawerLayout
    private fun setupDrawerAndToolbar() {
        // Establece el Toolbar como el ActionBar de la Activity
        setSupportActionBar(binding.content.toolbar)

        // Configura el menú hamburguesa que abre/cierra el Drawer
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.content.toolbar,
            R.string.navigation_drawer_open, // Necesitas estos strings en res/values/strings.xml
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Establece el listener para manejar las selecciones del menú lateral
        binding.navView.setNavigationItemSelectedListener(this)
    }

    // Método para mostrar el saludo con el nombre del usuario
    private fun displayGreeting() {
        val userName = auth.currentUser?.displayName ?: "Usuario"
        // Actualiza el TextView que está dentro del Toolbar (binding.content.toolbar)
        binding.content.tvGreeting.text = "Hola, $userName. ¡Bienvenido!"
    }


    // Método para configurar el RecyclerView con los productos
    private fun setupProductCatalog() {
        // 1. Crear una lista de productos de ejemplo (MOCK DATA)
        val sampleProducts = listOf(
            // Nota: Debes agregar estas imágenes (R.drawable.nombre) a tu carpeta 'drawable'
            Product(1, R.drawable.orisol, "Bidón de Aceite OROSI", 38.00),
            Product(2, R.drawable.mantecaorosi, "Caja de Manteca OROSI", 47.00),
            Product(3, R.drawable.jaleadepina, "Caja de Jalea Piña Universal", 21.00),
            Product(4, R.drawable.azucar, "Azúcar de 5lb Fardo", 23.00)
        )

        // 2. Inicializar el Adaptador
        val adapter = ProductAdapter(sampleProducts) { product ->
            // Acción al hacer clic en el botón "Comprar"
            Toast.makeText(this, "Agregado al Carrito: ${product.name}", Toast.LENGTH_SHORT).show()
            // Aquí se agregaría la lógica para añadir el producto a un Carrito (siguiente fase)
        }

        // 3. Configurar el RecyclerView
        binding.content.rvProducts.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            this.adapter = adapter
        }
    }

    // --- 4. MANEJO DE CLICKS EN EL MENÚ LATERAL ---

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Maneja las opciones del menú lateral (nav_view)
        when (item.itemId) {
            R.id.nav_home -> {
                // Ya estamos en el Home (Catálogo)
                Toast.makeText(this, "Catálogo Principal", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_cart -> {
                startActivity(Intent(this, CartActivity::class.java))
                binding.drawerLayout.closeDrawers()
                return true
            }

            R.id.nav_logout -> {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        // Cierra el menú lateral después de la selección
        binding.drawerLayout.closeDrawers()
        return true
    }
}
