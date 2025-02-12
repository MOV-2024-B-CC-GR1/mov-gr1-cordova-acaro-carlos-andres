package com.example.recipeapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class EditarActivity : AppCompatActivity() {
    private var itemId: Int = 0
    private var tipo: String = ""
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        itemId = intent.getIntExtra("itemId", 0)
        tipo = intent.getStringExtra("tipo") ?: ""

        val editTextNombre = findViewById<EditText>(R.id.editTextNombre)
        val editTextCantidad = findViewById<EditText>(R.id.editTextCantidad)
        val editTextTiempo = findViewById<EditText>(R.id.editTextTiempo)
        val editTextInstrucciones = findViewById<EditText>(R.id.editTextInstrucciones)
        val editTextLatitud = findViewById<EditText>(R.id.editTextLatitud)
        val editTextLongitud = findViewById<EditText>(R.id.editTextLongitud)

        // Configurar visibilidad según el tipo
        if (tipo == "ingrediente") {
            editTextNombre.setText(intent.getStringExtra("nombre"))
            editTextCantidad.setText(intent.getStringExtra("cantidad"))
            editTextTiempo.visibility = View.GONE
            editTextInstrucciones.visibility = View.GONE
            editTextLatitud.visibility = View.GONE
            editTextLongitud.visibility = View.GONE
        } else {
            editTextNombre.setText(intent.getStringExtra("nombre"))
            editTextTiempo.setText(intent.getIntExtra("tiempo", 0).toString())
            editTextInstrucciones.setText(intent.getStringExtra("instrucciones"))
            editTextLatitud.setText(intent.getDoubleExtra("latitud", 0.0).toString())
            editTextLongitud.setText(intent.getDoubleExtra("longitud", 0.0).toString())
            editTextCantidad.visibility = View.GONE
        }

        findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            val nuevoNombre = editTextNombre.text.toString()

            if (tipo == "ingrediente") {
                val nuevaCantidad = editTextCantidad.text.toString()
                Repositorio.editarIngrediente(this, itemId, nuevoNombre, nuevaCantidad)
            } else {
                val nuevoTiempo = editTextTiempo.text.toString().toIntOrNull() ?: 0
                val nuevasInstrucciones = editTextInstrucciones.text.toString()
                val nuevaLatitud = editTextLatitud.text.toString().toDoubleOrNull() ?: 0.0
                val nuevaLongitud = editTextLongitud.text.toString().toDoubleOrNull() ?: 0.0
                Repositorio.editarReceta(this, itemId, nuevoNombre, nuevoTiempo,
                    nuevasInstrucciones, nuevaLatitud, nuevaLongitud)
            }
            finish()
        }
    }

    private fun obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    latitud = it.latitude
                    longitud = it.longitude
                    Toast.makeText(this, "Ubicación guardada", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    obtenerUbicacionActual()
                }
            }
        }
    }
}