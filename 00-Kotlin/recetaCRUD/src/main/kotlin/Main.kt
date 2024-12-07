package main

import Interfaz.MainWindow
import filemanager.FileManager
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {
    try {
        // Inicializar archivos de almacenamiento
        FileManager.inicializarArchivos()

        // Establecer el look and feel del sistema operativo
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

        // Iniciar la aplicación en el Event Dispatch Thread
        SwingUtilities.invokeLater {
            // Crear y mostrar la ventana principal
            MainWindow().apply {
                isVisible = true
            }
        }
    } catch (e: Exception) {
        // Manejar cualquier error durante la inicialización
        println("Error al iniciar la aplicación: ${e.message}")
        e.printStackTrace()
    }
}