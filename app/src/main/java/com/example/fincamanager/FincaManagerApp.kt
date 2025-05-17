package com.example.fincamanager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FincaManagerApp : Application() {
    // El contenedor de Hilt se adjunta a la vida útil de la Application y proporciona dependencias
    // a todas las demás clases de la aplicación según sea necesario.
    
    override fun onCreate() {
        super.onCreate()
        // Inicialización adicional si es necesario
    }
} 