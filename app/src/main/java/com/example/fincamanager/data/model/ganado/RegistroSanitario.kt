package com.example.fincamanager.data.model.ganado

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

/**
 * Modelo que representa un registro sanitario para un animal.
 * 
 * Este modelo permite almacenar información sobre tratamientos médicos,
 * vacunas, diagnósticos y otras intervenciones sanitarias realizadas a los animales.
 */
@Entity(
    tableName = "registros_sanitarios",
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
data class RegistroSanitario(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    // Relación con el animal
    val animalId: String,
    
    // Datos del registro
    val fecha: Date = Date(),
    val tipo: TipoRegistroSanitario = TipoRegistroSanitario.OTRO,
    val descripcion: String = "",
    
    // Datos del tratamiento o vacuna
    val producto: String = "", // Nombre del medicamento o vacuna
    val dosis: String = "", // Dosis aplicada
    val viaMedicacion: String = "", // Intramuscular, subcutánea, oral, etc.
    
    // Personal que realiza la intervención
    val responsable: String = "", // Veterinario o personal que realiza el tratamiento
    
    // Datos de seguimiento
    val observaciones: String = "",
    val fechaProximoTratamiento: Date? = null, // Fecha para tratamiento de seguimiento
    
    // Datos de trazabilidad y sincronización
    val fechaCreacion: Date = Date(),
    val fechaActualizacion: Date = Date(),
    val sincronizado: Boolean = false
)

/**
 * Enumeración que representa los tipos de registros sanitarios.
 */
enum class TipoRegistroSanitario {
    VACUNACION,
    DESPARASITACION,
    TRATAMIENTO,
    DIAGNOSTICO,
    REVISION,
    CIRUGIA,
    OTRO
} 