package models

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Receta(
    var id: Int = 0,
    var nombre: String = "",
    var numeroTotalIngredientes: Int = 0,
    @Serializable(with = LocalDateSerializer::class)
    var fechaCreacion: LocalDate = LocalDate.now(),
    var esVegana: Boolean = false,
    var costoEstimado: Double = 0.0,
    var ingredientes: MutableList<Ingrediente> = mutableListOf()
) {

    fun actualizarCalculos() {
        numeroTotalIngredientes = ingredientes.size
        costoEstimado = ingredientes.sumOf { it.cantidad * it.costoUnitario }
    }
}

@Serializable
data class Ingrediente(
    var id: Int = 0,
    var nombre: String = "",
    var cantidad: Double = 0.0,
    var unidadMedida: String = "",
    var esPrincipal: Boolean = false,
    var costoUnitario: Double = 0.0, // Agregado para calcular el costo total
    var recetaId: Int = 0
)