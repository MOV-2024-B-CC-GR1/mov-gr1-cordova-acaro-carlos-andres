package com.example.recipeapp

data class Ingredient(
    val id: Int,
    var nombre: String,
    var cantidad: String,
    var recetaId: Int
)