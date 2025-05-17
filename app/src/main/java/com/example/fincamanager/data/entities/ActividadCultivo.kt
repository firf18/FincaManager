package com.example.fincamanager.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "actividades_cultivo",
    foreignKeys = [
        ForeignKey(
            entity = Cultivo::class,
            parentColumns = ["id"],
            childColumns = ["cultivoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cultivoId")]
)
data class ActividadCultivo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cultivoId: Long,
    val fecha: Date,
    val tipo: TipoActividadCultivo,
    val descripcion: String,
    val insumos: String? = null,  // Insumos utilizados
    val cantidadInsumo: Double? = null,
    val unidadMedida: String? = null,
    val costo: Double = 0.0,
    val responsable: String,
    val observaciones: String? = null
)

enum class TipoActividadCultivo {
    RIEGO,
    FERTILIZACION,
    FUMIGACION,
    DESHIERBE,
    PODA,
    COSECHA,
    CONTROL_PLAGAS,
    MANTENIMIENTO,
    OTRO
} 