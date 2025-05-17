package com.example.fincamanager.data.model.cultivo

import java.util.Date
import java.util.UUID

/**
 * Modelo de datos para un registro de cosecha de cultivos
 */
data class RegistroCosecha(
    val id: String = UUID.randomUUID().toString(),
    val cultivoId: String = "",
    val fecha: Date = Date(),
    val cantidad: Double = 0.0,
    val unidadMedida: String = "",
    val calidad: CalidadCosecha = CalidadCosecha.BUENA,
    val destinoProducto: String = "",
    val precioVenta: Double? = null,
    val responsable: String = "",
    val observaciones: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

/**
 * Enumerado para la calidad de la cosecha
 */
enum class CalidadCosecha {
    EXCELENTE,
    BUENA,
    REGULAR,
    BAJA,
    DAÑADA
} 