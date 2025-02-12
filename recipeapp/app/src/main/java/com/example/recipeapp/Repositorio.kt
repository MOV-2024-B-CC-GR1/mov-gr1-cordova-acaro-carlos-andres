package com.example.recipeapp

import android.content.ContentValues
import android.content.Context

object Repositorio {
    private lateinit var dbHelper: DatabaseHelper

    fun init(context: Context) {
        dbHelper = DatabaseHelper(context)
    }

    // Funciones para Recetas
    fun agregarReceta(context: Context, receta: Recipe): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_RECETA_NOMBRE, receta.nombre)
            put(DatabaseHelper.COL_RECETA_TIEMPO, receta.tiempoPreparacion)
            put(DatabaseHelper.COL_RECETA_INSTRUCCIONES, receta.instrucciones)
            put(DatabaseHelper.COL_RECETA_LATITUD, receta.latitud)
            put(DatabaseHelper.COL_RECETA_LONGITUD, receta.longitud)
        }
        return db.insert(DatabaseHelper.TABLE_RECETAS, null, values)
    }

    fun obtenerRecetas(): List<Recipe> {
        val recetas = mutableListOf<Recipe>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_RECETAS,
            null,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val receta = Recipe(
                    getInt(getColumnIndexOrThrow(DatabaseHelper.COL_RECETA_ID)),
                    getString(getColumnIndexOrThrow(DatabaseHelper.COL_RECETA_NOMBRE)),
                    getInt(getColumnIndexOrThrow(DatabaseHelper.COL_RECETA_TIEMPO)),
                    getString(getColumnIndexOrThrow(DatabaseHelper.COL_RECETA_INSTRUCCIONES)),
                    getDouble(getColumnIndexOrThrow(DatabaseHelper.COL_RECETA_LATITUD)),
                    getDouble(getColumnIndexOrThrow(DatabaseHelper.COL_RECETA_LONGITUD))
                )
                recetas.add(receta)
            }
        }
        cursor.close()
        return recetas
    }

    fun eliminarReceta(context: Context, id: Int) {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_RECETAS, "${DatabaseHelper.COL_RECETA_ID} = ?", arrayOf(id.toString()))
    }

    fun editarReceta(context: Context, id: Int, nuevoNombre: String,
                     nuevoTiempo: Int, nuevasInstrucciones: String,
                     latitud: Double, longitud: Double) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_RECETA_NOMBRE, nuevoNombre)
            put(DatabaseHelper.COL_RECETA_TIEMPO, nuevoTiempo)
            put(DatabaseHelper.COL_RECETA_INSTRUCCIONES, nuevasInstrucciones)
            put(DatabaseHelper.COL_RECETA_LATITUD, latitud)
            put(DatabaseHelper.COL_RECETA_LONGITUD, longitud)
        }
        db.update(DatabaseHelper.TABLE_RECETAS, values,
            "${DatabaseHelper.COL_RECETA_ID} = ?", arrayOf(id.toString()))
    }

    // Funciones para Ingredientes
    fun agregarIngrediente(context: Context, ingrediente: Ingredient): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_ING_NOMBRE, ingrediente.nombre)
            put(DatabaseHelper.COL_ING_CANTIDAD, ingrediente.cantidad)
            put(DatabaseHelper.COL_ING_RECETA_ID, ingrediente.recetaId)
        }
        return db.insert(DatabaseHelper.TABLE_INGREDIENTES, null, values)
    }

    fun obtenerIngredientesDeReceta(recetaId: Int): List<Ingredient> {
        val ingredientes = mutableListOf<Ingredient>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_INGREDIENTES,
            null,
            "${DatabaseHelper.COL_ING_RECETA_ID} = ?",
            arrayOf(recetaId.toString()),
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val ingrediente = Ingredient(
                    getInt(getColumnIndexOrThrow(DatabaseHelper.COL_ING_ID)),
                    getString(getColumnIndexOrThrow(DatabaseHelper.COL_ING_NOMBRE)),
                    getString(getColumnIndexOrThrow(DatabaseHelper.COL_ING_CANTIDAD)),
                    getInt(getColumnIndexOrThrow(DatabaseHelper.COL_ING_RECETA_ID))
                )
                ingredientes.add(ingrediente)
            }
        }
        cursor.close()
        return ingredientes
    }

    fun eliminarIngrediente(context: Context, id: Int) {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_INGREDIENTES, "${DatabaseHelper.COL_ING_ID} = ?", arrayOf(id.toString()))
    }

    fun editarIngrediente(context: Context, id: Int, nuevoNombre: String, nuevaCantidad: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_ING_NOMBRE, nuevoNombre)
            put(DatabaseHelper.COL_ING_CANTIDAD, nuevaCantidad)
        }
        db.update(
            DatabaseHelper.TABLE_INGREDIENTES,
            values,
            "${DatabaseHelper.COL_ING_ID} = ?",
            arrayOf(id.toString())
        )
    }
}