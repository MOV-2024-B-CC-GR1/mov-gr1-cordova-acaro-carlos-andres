package com.example.recipeapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "recetas.db"
        const val DATABASE_VERSION = 2

        const val TABLE_RECETAS = "recetas"
        const val TABLE_INGREDIENTES = "ingredientes"

        const val COL_RECETA_ID = "id"
        const val COL_RECETA_NOMBRE = "nombre"
        const val COL_RECETA_TIEMPO = "tiempo_preparacion"
        const val COL_RECETA_INSTRUCCIONES = "instrucciones"
        const val COL_RECETA_LATITUD = "latitud"
        const val COL_RECETA_LONGITUD = "longitud"

        const val COL_ING_ID = "id"
        const val COL_ING_NOMBRE = "nombre"
        const val COL_ING_CANTIDAD = "cantidad"
        const val COL_ING_RECETA_ID = "receta_id"

    }

    override fun onCreate(db: SQLiteDatabase) {
        val createRecetas = """
            CREATE TABLE $TABLE_RECETAS (
                $COL_RECETA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_RECETA_NOMBRE TEXT NOT NULL,
                $COL_RECETA_TIEMPO INTEGER NOT NULL,
                $COL_RECETA_INSTRUCCIONES TEXT NOT NULL,
                $COL_RECETA_LATITUD REAL,
                $COL_RECETA_LONGITUD REAL
            )
        """.trimIndent()

        val createIngredientes = """
            CREATE TABLE $TABLE_INGREDIENTES (
                $COL_ING_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ING_NOMBRE TEXT NOT NULL,
                $COL_ING_CANTIDAD TEXT NOT NULL,
                $COL_ING_RECETA_ID INTEGER NOT NULL,
                FOREIGN KEY($COL_ING_RECETA_ID) REFERENCES $TABLE_RECETAS($COL_RECETA_ID) ON DELETE CASCADE
            )
        """.trimIndent()

        db.execSQL(createRecetas)
        db.execSQL(createIngredientes)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Para manejar la actualización de manera segura
            // Primero creamos una tabla temporal con la nueva estructura
            val tempTable = """
                CREATE TABLE temp_recetas (
                    $COL_RECETA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COL_RECETA_NOMBRE TEXT NOT NULL,
                    $COL_RECETA_TIEMPO INTEGER NOT NULL,
                    $COL_RECETA_INSTRUCCIONES TEXT NOT NULL,
                    $COL_RECETA_LATITUD REAL,
                    $COL_RECETA_LONGITUD REAL
                )
            """.trimIndent()

            try {
                // Crear tabla temporal
                db.execSQL(tempTable)

                // Copiar datos existentes
                db.execSQL("""
                    INSERT INTO temp_recetas ($COL_RECETA_ID, $COL_RECETA_NOMBRE, 
                    $COL_RECETA_TIEMPO, $COL_RECETA_INSTRUCCIONES)
                    SELECT $COL_RECETA_ID, $COL_RECETA_NOMBRE, 
                    $COL_RECETA_TIEMPO, $COL_RECETA_INSTRUCCIONES
                    FROM $TABLE_RECETAS
                """.trimIndent())

                // Eliminar tabla vieja
                db.execSQL("DROP TABLE IF EXISTS $TABLE_RECETAS")

                // Renombrar tabla temporal
                db.execSQL("ALTER TABLE temp_recetas RENAME TO $TABLE_RECETAS")
            } catch (e: Exception) {
                // Si es una instalación nueva, simplemente creamos las tablas
                onCreate(db)
            }
        }
    }
}