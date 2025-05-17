package com.example.fincamanager.data.model.cultivo

import java.util.Date
import java.util.UUID

/**
 * Modelo de datos para un registro de actividad en cultivos
 */
data class RegistroActividad(
    val id: String = UUID.randomUUID().toString(),
    val cultivoId: String = "",
    val tipoActividad: TipoActividad = TipoActividad.OTRO,
    val fecha: Date = Date(),
    val descripcion: String = "",
    val insumos: List<Insumo> = emptyList(),
    val costoTotal: Double = 0.0,
    val responsable: String = "",
    val observaciones: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

/**
 * Enumerado para los tipos de actividades en cultivos
 */
enum class TipoActividad {
    SIEMBRA,
    FERTILIZACION,
    RIEGO,
    FUMIGACION,
    CONTROL_MALEZAS,
    CONTROL_PLAGAS,
    PODA,
    COSECHA,
    OTRO
}

/**
 * Modelo para los insumos utilizados en una actividad
 */
data class Insumo(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String = "",
    val cantidad: Double = 0.0,
    val unidadMedida: String = "",
    val costo: Double = 0.0
) 