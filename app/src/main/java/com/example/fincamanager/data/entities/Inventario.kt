package com.example.fincamanager.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "inventario")
data class Inventario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val categoria: CategoriaInventario,
    val descripcion: String? = null,
    val cantidad: Double,
    val unidadMedida: String,
    val precioUnitario: Double,
    val valorTotal: Double,
    val ubicacion: String,      // Ubicación en la finca
    val fechaCompra: Date? = null,
    val fechaVencimiento: Date? = null,  // Para insumos con caducidad
    val stockMinimo: Double? = null,     // Cantidad mínima recomendada
    val proveedor: String? = null,
    val estado: String,         // Activo, Inactivo, En reparación, etc.
    val observaciones: String? = null,
    val ultimaActualizacion: Date = Date()
)

enum class CategoriaInventario {
    INSUMO_AGRICOLA,      // Semillas, fertilizantes, etc.
    INSUMO_GANADERO,      // Alimentos, medicamentos para animales, etc.
    MAQUINARIA,           // Tractores, cosechadoras, etc.
    HERRAMIENTA,          // Herramientas manuales o de menor tamaño
    EQUIPO,               // Equipos diversos
    PRODUCTO_COSECHADO,   // Productos ya cosechados
    PRODUCTO_ANIMAL,      // Productos de origen animal (leche, etc.)
    COMBUSTIBLE,          // Gasolina, diésel, etc.
    REPUESTO,             // Repuestos para maquinaria y equipos
    OTRO                  
} 