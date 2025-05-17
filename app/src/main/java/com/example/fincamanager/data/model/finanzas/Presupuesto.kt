package com.example.fincamanager.data.model.finanzas

import java.util.Date
import java.util.UUID

/**
 * Modelo de datos para un presupuesto
 */
data class Presupuesto(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String = "",
    val montoAsignado: Double = 0.0,
    val montoGastado: Double = 0.0,
    val fechaInicio: Date = Date(),
    val fechaFin: Date = Date(),
    val categoria: String = "",
    val area: AreaFinca = AreaFinca.GENERAL,
    val idReferencia: String = "", // ID de animal, cultivo u otro elemento relacionado
    val fincaId: String = "",
    val notas: String = "",
    val activo: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) 