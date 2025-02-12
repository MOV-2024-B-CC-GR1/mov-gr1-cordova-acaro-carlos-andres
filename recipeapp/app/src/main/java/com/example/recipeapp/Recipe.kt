package com.example.recipeapp

data class Recipe(
    val id: Int,
    var nombre: String,
    var tiempoPreparacion: Int,
    var instrucciones: String,
    var latitud: Double? = null,
    var longitud: Double? = null
)