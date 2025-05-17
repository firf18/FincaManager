package com.example.fincamanager.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "produccion_animal",
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
data class ProduccionAnimal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val animalId: Long,
    val fecha: Date,
    val tipoProduccion: TipoProduccion,
    val cantidad: Double,
    val unidad: String,  // litros, kilogramos, etc.
    val observaciones: String? = null,
    val registradoPor: String
)

enum class TipoProduccion {
    LECHE,
    CARNE,
    HUEVOS,
    LANA,
    CRIA,
    OTRO
} 