package com.example.fincamanager.data.model.finanzas

import java.util.Date
import java.util.UUID

/**
 * Modelo de datos para una transacción financiera
 */
data class Transaccion(
    val id: String = UUID.randomUUID().toString(),
    val fecha: Date = Date(),
    val tipo: TipoTransaccion = TipoTransaccion.GASTO,
    val categoria: String = "",
    val subcategoria: String = "",
    val monto: Double = 0.0,
    val descripcion: String = "",
    val metodoPago: String = "",
    val comprobante: String = "",
    val entidadRelacionada: String = "",
    val fincaId: String = "",
    val area: AreaFinca = AreaFinca.GENERAL,
    val idReferencia: String = "", // ID de animal, cultivo u otro elemento relacionado
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

/**
 * Enumerado para los tipos de transacción
 */
enum class TipoTransaccion {
    INGRESO,
    GASTO,
    TRANSFERENCIA,
    INVERSION,
    PRESTAMO,
    PAGO_PRESTAMO
}

/**
 * Enumerado para las áreas de la finca a la que se asocia la transacción
 */
enum class AreaFinca {
    GANADO,
    CULTIVO,
    MAQUINARIA,
    INFRAESTRUCTURA,
    PERSONAL,
    GENERAL
} 