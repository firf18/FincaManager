package com.example.fincamanager.data.model.cultivo

import java.util.Date
import java.util.UUID

/**
 * Modelo de datos para un cultivo
 */
data class Cultivo(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String = "",
    val variedad: String = "",
    val fechaSiembra: Date = Date(),
    val fechaCosechaEstimada: Date? = null,
    val areaEnHectareas: Double = 0.0,
    val ubicacionLote: String = "",
    val tipoSuelo: String = "",
    val estado: EstadoCultivo = EstadoCultivo.SEMBRADO,
    val notas: String = "",
    val fincaId: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

/**
 * Enumerado para los posibles estados de un cultivo
 */
enum class EstadoCultivo {
    SEMBRADO,
    EN_CRECIMIENTO,
    FLORECIMIENTO,
    FRUCTIFICACION,
    MADURACION,
    COSECHADO,
    ABANDONADO
} 