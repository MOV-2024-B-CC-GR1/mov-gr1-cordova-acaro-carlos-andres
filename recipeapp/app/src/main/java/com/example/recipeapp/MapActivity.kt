package com.example.recipeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private var nombreReceta: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        latitud = intent.getDoubleExtra("latitud", 0.0)
        longitud = intent.getDoubleExtra("longitud", 0.0)
        nombreReceta = intent.getStringExtra("nombre") ?: ""

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Si las coordenadas son 0,0 o inválidas, centrar en una ubicación por defecto
        // Por ejemplo, el centro de Ecuador
        val ubicacion = if (latitud == 0.0 && longitud == 0.0) {
            LatLng(-0.1806532, -78.4678382) // Coordenadas de Quito
        } else {
            LatLng(latitud, longitud)
        }

        googleMap.apply {
            addMarker(MarkerOptions()
                .position(ubicacion)
                .title(nombreReceta)
                .snippet("Lat: ${ubicacion.latitude}, Lng: ${ubicacion.longitude}"))

            moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15f))

            // Habilitar controles del mapa
            uiSettings.apply {
                isZoomControlsEnabled = true
                isCompassEnabled = true
                isMapToolbarEnabled = true
                isZoomGesturesEnabled = true
                isScrollGesturesEnabled = true
            }
        }
    }
}