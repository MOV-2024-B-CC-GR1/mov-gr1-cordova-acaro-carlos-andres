package org.example

import java.util.*

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val inmutable: String = "Carlos"

    var mutable: String = "Andrés"
    mutable = "Córdova"

    val ejVariable = "Carlos Córdova"
    ejVariable.trim()

    val edadEj: Int = 12

    val fechaNacimiento: Date = Date()

    val estadoCivilWhen: String = "S"

    when (estadoCivilWhen) {
        ("C") -> {
            println("Casado")
        }

        "S" -> {
            println("Soltero")
        }

        else -> {
            println("No sabemos")
        }
    }

    calcularSueldo(10.00)
    calcularSueldo(10.00, 15.00, 20.00)

    calcularSueldo(10.00, bonoEspecial = 20.00)
    calcularSueldo(bonoEspecial = 20.00, sueldo = 10.00, tasa = 15.30)

    val sumaA = Suma(1, 1)
    val sumaB = Suma(null, 1)
    val sumaC = Suma(1, null)
    val sumaD = Suma(null, null)
    sumaA.sumar()
    sumaB.sumar()
    sumaC.sumar()
    sumaD.sumar()
    println(Suma.pi)
    println(Suma.elevarAlCuadrdo(2))
    println(Suma.historialSumas)

    val arregloStatic: Array<Int> = arrayOf<Int>(1, 2, 3)
    println(arregloStatic);

    val arregloDinamico: ArrayList<Int> = arrayListOf<Int>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    println(arregloDinamico)
    arregloDinamico.add(11)
    arregloDinamico.add(12)
    println(arregloDinamico)

    val respuestaForEach: Unit = arregloDinamico
        .forEach { valorActual: Int ->
            println("Valor actual: ${valorActual}");
        }
    arregloDinamico.forEach { println("Valor Actual (it): ${it}") }

    val respuestaMap: List<Double> = arregloDinamico
        .map { valorActual: Int ->
            return@map valorActual.toDouble() + 100.00
        }
    println(respuestaMap)
    val respuestaMapDos = arregloDinamico.map { it + 15 }
    println(respuestaMapDos)

    val respuestaFilter: List<Int> = arregloDinamico
        .filter { valorActual: Int ->
            val mayoresACinco: Boolean = valorActual > 5
            return@filter mayoresACinco
        }
    val respuestaFilterDos = arregloDinamico.filter { it <= 5 }
    println(respuestaFilter)
    println(respuestaFilterDos)

    val respuestaAny: Boolean = arregloDinamico
        .any { valorActual: Int ->
            return@any (valorActual > 5)
        }
    println(respuestaAny)
    val respuestaAll: Boolean = arregloDinamico
        .all { valorActual: Int ->
            return@all (valorActual > 5)

        }
    println(respuestaAll)

    val respuesaReduce: Int = arregloDinamico
        .reduce { acumlado: Int, valorActual: Int ->
            return@reduce (acumlado + valorActual)
        }
    println(respuesaReduce)

}

fun imprimirNombre(nombre:String): Unit{
    fun otraFuncionAdentro(){
        println("Otra función adentro")
    }
    println("Nombre: ${nombre}")
}

fun calcularSueldo(
    sueldo: Double, // Requerido
    tasa: Double = 12.00, // Opcional (defecto)
    bonoEspecial: Double? = null // Opcional (nullable)
): Double {
    // Int -> Int? (nullable)
    // String -> String? (nullable)
    // Date -> Date? (nullable)

    if (bonoEspecial == null) {
        return sueldo * (100 / tasa)
    } else {
        return sueldo * (100 / tasa) * bonoEspecial
    }
}

abstract class Numeros( // Constructor Primario

protected val numeroUno: Int, // instancia.numeroUno
protected val numeroDos: Int, // instancia.numeroDos
){
init { // bloque constructor primario OPCIONAL
    this.numeroUno
    this.numeroDos
    println("Inicializando")
}
}

class Suma(
    unoParametro: Int,
    dosParametro: Int,
): Numeros(
    unoParametro,
    dosParametro
){
    public val soyPublicoExplicito: String = "Públicas"
    val soyPublicoImplicito: String = "Publico implicito"

    init{
        this.numeroUno
        this.numeroDos
        numeroUno
        numeroDos
        this.soyPublicoImplicito
        soyPublicoImplicito
    }

    constructor( // Constructor secundario
        uno: Int?, // Entero nullable
        dos: Int
    ):this(
        if(uno == null) 0 else uno,
        dos
    ){
        // Bloque de código de construcción secundaria
    }
    constructor( // Constructor secundario
        uno: Int,
        dos: Int?, // Entero nullable
    ):this(
        uno,
        if(dos == null) 0 else dos
    )
    constructor( // Constructor secundario
        uno: Int?, // Entero nullable
        dos: Int?, // Entero nullable
    ):this(
        if (uno == null) 0 else uno,
        if (dos == null) 0 else dos
    )

    fun sumar ():Int{
        val total = numeroUno + numeroDos
        agregarHstorial(total)
        return total
    }

    companion object{
        val pi = 3.14159

        fun elevarAlCuadrdo(num:Int):Int{return num * num}
        val historialSumas = arrayListOf<Int>()

        fun agregarHstorial(valorTotalSuma:Int){
            historialSumas.add(valorTotalSuma)
        }
    }


}

