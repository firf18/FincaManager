package com.example.fincamanager.util

import android.content.Context
import android.util.Log
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Clase que implementa un servidor HTTP simple para gestionar actualizaciones en vivo.
 */
class LiveReloadServer(context: Context) {
    private val TAG = "LiveReloadServer"
    private var server: NanoServer? = null
    private var lastChangeTimestamp = System.currentTimeMillis()
    
    init {
        startServer()
    }
    
    private fun startServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                server = NanoServer(8081).apply { 
                    start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
                }
                Log.d(TAG, "Servidor LiveReload iniciado en el puerto 8081")
            } catch (e: IOException) {
                Log.e(TAG, "Error al iniciar el servidor LiveReload", e)
            }
        }
    }
    
    fun notifyChange() {
        lastChangeTimestamp = System.currentTimeMillis()
    }
    
    fun stop() {
        server?.stop()
        Log.d(TAG, "Servidor LiveReload detenido")
    }
    
    /**
     * Servidor HTTP minimalista que responde a las peticiones de estado
     */
    private inner class NanoServer(port: Int) : NanoHTTPD(port) {
        override fun serve(session: IHTTPSession?): Response {
            if (session?.uri == "/status") {
                return newFixedLengthResponse(Response.Status.OK, "application/json", 
                    "{\"timestamp\": $lastChangeTimestamp}")
            }
            
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, 
                "Ruta no encontrada")
        }
    }
} 