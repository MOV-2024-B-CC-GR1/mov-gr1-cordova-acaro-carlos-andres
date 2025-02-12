package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecipesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = RecipeAdapter(Repositorio.obtenerRecetas(), ::onRecetaClick)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.btnAgregarReceta).setOnClickListener {
            val id = (Repositorio.obtenerRecetas().size + 1)
            val nuevaReceta = Recipe(
                id = id,
                nombre = "Nueva Receta",
                tiempoPreparacion = 30,
                instrucciones = "Instrucciones aquí",
                latitud = 0.0,
                longitud = 0.0
            )
            Repositorio.agregarReceta(this, nuevaReceta)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter = RecipeAdapter(Repositorio.obtenerRecetas(), ::onRecetaClick)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun onRecetaClick(receta: Recipe) {
        val options = arrayOf("Editar", "Eliminar", "Ver ingredientes", "Ver en mapa")
        AlertDialog.Builder(this)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> editarReceta(receta)
                    1 -> eliminarReceta(receta)
                    2 -> verIngredientes(receta)
                    3 -> verEnMapa(receta)
                }
            }
            .show()
    }

    private fun verEnMapa(receta: Recipe) {
        // Solo mostrar el mapa si las coordenadas son válidas
        if (receta.latitud == 0.0 && receta.longitud == 0.0) {
            Toast.makeText(this,
                "Esta receta no tiene coordenadas guardadas",
                Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra("latitud", receta.latitud)
        intent.putExtra("longitud", receta.longitud)
        intent.putExtra("nombre", receta.nombre)
        startActivity(intent)
    }

    private fun editarReceta(receta: Recipe) {
        val intent = Intent(this, EditarActivity::class.java)
        intent.putExtra("itemId", receta.id)
        intent.putExtra("nombre", receta.nombre)
        intent.putExtra("tiempo", receta.tiempoPreparacion)
        intent.putExtra("instrucciones", receta.instrucciones)
        intent.putExtra("latitud", receta.latitud)
        intent.putExtra("longitud", receta.longitud)
        intent.putExtra("tipo", "receta")
        startActivity(intent)
    }

    private fun eliminarReceta(receta: Recipe) {
        Repositorio.eliminarReceta(this, receta.id)
        adapter.notifyDataSetChanged()
    }

    private fun verIngredientes(receta: Recipe) {
        val intent = Intent(this, IngredientsActivity::class.java)
        intent.putExtra("recetaId", receta.id)
        startActivity(intent)
    }
}