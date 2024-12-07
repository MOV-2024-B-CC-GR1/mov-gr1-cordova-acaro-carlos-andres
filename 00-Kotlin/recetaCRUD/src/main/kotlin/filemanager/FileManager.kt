package filemanager

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import models.Ingrediente
import models.Receta
import java.io.File

object FileManager {
    private const val RECETAS_FILE = "recetas.txt"
    private const val INGREDIENTES_FILE = "ingredientes.txt"

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    // Métodos para Recetas
    fun guardarReceta(receta: Receta) {
        val recetas = cargarRecetas().toMutableList()
        if (receta.id == 0) {
            receta.id = (recetas.maxOfOrNull { it.id } ?: 0) + 1
        } else {
            recetas.removeIf { it.id == receta.id }
        }
        recetas.add(receta)
        File(RECETAS_FILE).writeText(json.encodeToString(recetas))
    }

    fun cargarRecetas(): List<Receta> {
        val file = File(RECETAS_FILE)
        if (!file.exists()) return emptyList()
        return try {
            json.decodeFromString(file.readText())
        } catch (e: Exception) {
            println("Error al cargar recetas: ${e.message}")
            emptyList()
        }
    }

    fun obtenerReceta(id: Int): Receta? {
        return cargarRecetas().find { it.id == id }
    }

    fun eliminarReceta(id: Int) {
        val recetas = cargarRecetas().toMutableList()
        recetas.removeIf { it.id == id }
        File(RECETAS_FILE).writeText(json.encodeToString(recetas))

        // Eliminar ingredientes asociados
        val ingredientes = cargarIngredientes().toMutableList()
        ingredientes.removeIf { it.recetaId == id }
        File(INGREDIENTES_FILE).writeText(json.encodeToString(ingredientes))
    }

    // Métodos para Ingredientes
    fun guardarIngrediente(ingrediente: Ingrediente) {
        val ingredientes = cargarIngredientes().toMutableList()
        val ingredientesReceta = ingredientes.filter { it.recetaId == ingrediente.recetaId }

        if (ingrediente.id == 0) {
            // Asignar el siguiente ID disponible para esta receta específica
            ingrediente.id = (ingredientesReceta.maxOfOrNull { it.id } ?: 0) + 1
        } else {
            // Si es una actualización, mantener el mismo ID pero eliminar el anterior
            ingredientes.removeIf { it.id == ingrediente.id && it.recetaId == ingrediente.recetaId }
        }
        ingredientes.add(ingrediente)
        File(INGREDIENTES_FILE).writeText(json.encodeToString(ingredientes))
    }

    fun cargarIngredientes(): List<Ingrediente> {
        val file = File(INGREDIENTES_FILE)
        if (!file.exists()) return emptyList()
        return try {
            json.decodeFromString(file.readText())
        } catch (e: Exception) {
            println("Error al cargar ingredientes: ${e.message}")
            emptyList()
        }
    }

    fun obtenerIngredientesDeReceta(recetaId: Int): List<Ingrediente> {
        return cargarIngredientes()
            .filter { it.recetaId == recetaId }
            .sortedBy { it.id }  // Asegurar que se muestren en orden por ID
    }

    fun eliminarIngrediente(id: Int) {
        val ingredientes = cargarIngredientes().toMutableList()
        val ingredienteAEliminar = ingredientes.find { it.id == id }

        if (ingredienteAEliminar != null) {
            val recetaId = ingredienteAEliminar.recetaId
            // Eliminar el ingrediente
            ingredientes.removeIf { it.id == id }

            // Reindexar los ingredientes restantes de la misma receta
            val ingredientesReceta = ingredientes
                .filter { it.recetaId == recetaId }
                .sortedBy { it.id }
                .mapIndexed { index, ingr ->
                    ingr.copy(id = index + 1)
                }

            // Actualizar la lista completa
            ingredientes.removeIf { it.recetaId == recetaId }
            ingredientes.addAll(ingredientesReceta)

            File(INGREDIENTES_FILE).writeText(json.encodeToString(ingredientes))
        }
    }

    fun reindexarIngredientesDeReceta(recetaId: Int) {
        val ingredientes = cargarIngredientes().toMutableList()
        val otrosIngredientes = ingredientes.filter { it.recetaId != recetaId }
        val ingredientesReceta = ingredientes
            .filter { it.recetaId == recetaId }
            .sortedBy { it.id }
            .mapIndexed { index, ingrediente ->
                ingrediente.copy(id = index + 1)
            }

        ingredientes.clear()
        ingredientes.addAll(otrosIngredientes)
        ingredientes.addAll(ingredientesReceta)

        File(INGREDIENTES_FILE).writeText(json.encodeToString(ingredientes))
    }

    fun inicializarArchivos() {
        val recetasFile = File(RECETAS_FILE)
        val ingredientesFile = File(INGREDIENTES_FILE)

        if (!recetasFile.exists()) {
            recetasFile.writeText("[]")
        }
        if (!ingredientesFile.exists()) {
            ingredientesFile.writeText("[]")
        }
    }
}