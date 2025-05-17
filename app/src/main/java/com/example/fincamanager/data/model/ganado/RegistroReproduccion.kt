package com.example.fincamanager.data.model.ganado

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

/**
 * Modelo que representa un registro reproductivo para un animal.
 * 
 * Este modelo permite almacenar información sobre eventos reproductivos,
 * como celos, inseminaciones, gestaciones, partos, etc.
 */
@Entity(
    tableName = "registros_reproduccion",
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
data class RegistroReproduccion(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    // Relación con el animal (normalmente la hembra)
    val animalId: String,
    
    // Datos del evento reproductivo
    val fecha: Date = Date(),
    val tipoEvento: TipoEventoReproductivo = TipoEventoReproductivo.OTRO,
    
    // Para eventos de tipo MONTA o INSEMINACION
    val idMacho: String = "", // ID del reproductor (para monta natural)
    val tipoSemen: String = "", // Tipo/raza de semen usado (para inseminación)
    val inseminador: String = "", // Persona que realiza la inseminación
    
    // Para eventos de tipo PARTO
    val cantidadCrias: Int = 0,
    val idsCrias: List<String> = emptyList(), // IDs de las crías registradas
    val complicaciones: String = "",
    
    // Para eventos de tipo DIAGNOSTICO_GESTACION
    val resultado: Boolean? = null, // true = positivo, false = negativo, null = pendiente
    val metodoDiagnostico: String = "", // Palpación, ecografía, etc.
    val diasGestacion: Int? = null,
    val fechaProbableParto: Date? = null,
    
    // Observaciones generales
    val observaciones: String = "",
    
    // Datos de trazabilidad y sincronización
    val fechaCreacion: Date = Date(),
    val fechaActualizacion: Date = Date(),
    val sincronizado: Boolean = false
)

/**
 * Enumeración que representa los tipos de eventos reproductivos.
 */
enum class TipoEventoReproductivo {
    CELO,
    MONTA,
    INSEMINACION,
    DIAGNOSTICO_GESTACION,
    PARTO,
    ABORTO,
    OTRO
} 