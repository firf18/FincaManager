package com.example.fincamanager.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "eventos_salud_animal",
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
data class EventoSaludAnimal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val animalId: Long,
    val fecha: Date,
    val tipo: TipoEventoSalud,
    val descripcion: String,
    val responsable: String,
    val costo: Double = 0.0,
    val observaciones: String? = null
)

enum class TipoEventoSalud {
    VACUNACION,
    DESPARASITACION,
    TRATAMIENTO_MEDICO,
    ENFERMEDAD,
    CONSULTA_VETERINARIA,
    OTRO
} 