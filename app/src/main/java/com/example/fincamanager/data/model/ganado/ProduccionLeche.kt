package com.example.fincamanager.data.model.ganado

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

/**
 * Modelo que representa un registro de producción de leche para un animal.
 * 
 * Este modelo permite almacenar información sobre la producción diaria
 * de leche de cada animal, así como datos relevantes sobre la calidad y el ordeño.
 */
@Entity(
    tableName = "produccion_leche",
    foreignKeys = [
        ForeignKey(
            entity = Animal::class,
            parentColumns = ["id"],
            childColumns = ["animalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("animalId")]
)
data class ProduccionLeche(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    // Relación con el animal
    val animalId: String,
    
    // Datos del ordeño
    val fecha: Date = Date(),
    val horario: HorarioOrdenio = HorarioOrdenio.MANANA,
    val cantidad: Double = 0.0, // Cantidad en litros
    
    // Datos de calidad (opcionales)
    val calidad: String = "", // Clasificación de calidad si aplica
    val porcentajeGrasa: Double? = null,
    val porcentajeProteina: Double? = null,
    
    // Observaciones
    val observaciones: String = "",
    
    // Datos de trazabilidad y sincronización
    val fechaCreacion: Date = Date(),
    val fechaActualizacion: Date = Date(),
    val sincronizado: Boolean = false
)

/**
 * Enumeración que representa los horarios de ordeño.
 */
enum class HorarioOrdenio {
    MANANA,
    TARDE,
    NOCHE,
    OTRO
} 