package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class IngredientsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IngredientAdapter
    private var recetaId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredients)

        // Configurar Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Ingredientes"

        recetaId = intent.getIntExtra("recetaId", 0)

        recyclerView = findViewById(R.id.recyclerView)
        val ingredientes = Repositorio.obtenerIngredientesDeReceta(recetaId)
        adapter = IngredientAdapter(ingredientes, ::onIngredienteClick)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<FloatingActionButton>(R.id.btnAgregarIngrediente).setOnClickListener {
            val nuevoIngrediente = Ingredient(
                id = 0, // El ID será asignado por la base de datos
                nombre = "Nuevo Ingrediente",
                cantidad = "100g",
                recetaId = recetaId
            )
            Repositorio.agregarIngrediente(this, nuevoIngrediente)
            adapter.actualizarIngredientes(Repositorio.obtenerIngredientesDeReceta(recetaId))
        }
    }

    private fun onIngredienteClick(ingrediente: Ingredient) {
        val options = arrayOf("Editar", "Eliminar")
        AlertDialog.Builder(this)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> editarIngrediente(ingrediente)
                    1 -> eliminarIngrediente(ingrediente)
                }
            }
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        adapter.actualizarIngredientes(Repositorio.obtenerIngredientesDeReceta(recetaId))
    }

    private fun editarIngrediente(ingrediente: Ingredient) {
        val intent = Intent(this, EditarActivity::class.java)
        intent.putExtra("itemId", ingrediente.id)
        intent.putExtra("nombre", ingrediente.nombre)
        intent.putExtra("cantidad", ingrediente.cantidad)
        intent.putExtra("tipo", "ingrediente")
        startActivity(intent)
    }

    private fun eliminarIngrediente(ingrediente: Ingredient) {
        Repositorio.eliminarIngrediente(this, ingrediente.id)
        adapter.actualizarIngredientes(Repositorio.obtenerIngredientesDeReceta(recetaId))
    }
}