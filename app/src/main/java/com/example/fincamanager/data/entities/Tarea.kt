package com.example.fincamanager.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "tareas",
    foreignKeys = [
        ForeignKey(
            entity = Empleado::class,
            parentColumns = ["id"],
            childColumns = ["empleadoId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("empleadoId")]
)
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val descripcion: String,
    val fechaCreacion: Date = Date(),
    val fechaVencimiento: Date,
    val prioridad: Prioridad,
    val estado: EstadoTarea,
    val empleadoId: Long? = null,  // Puede ser nula si aún no se ha asignado
    val categoria: String,         // Categoría o área de la tarea
    val ubicacion: String? = null, // Ubicación en la finca donde realizar la tarea
    val horasEstimadas: Double? = null,
    val observaciones: String? = null,
    val fechaCompletada: Date? = null
)

enum class Prioridad {
    ALTA,
    MEDIA,
    BAJA
}

enum class EstadoTarea {
    PENDIENTE,
    EN_PROCESO,
    COMPLETADA,
    CANCELADA
} 