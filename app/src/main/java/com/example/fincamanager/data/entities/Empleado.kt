package com.example.fincamanager.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "empleados")
data class Empleado(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val apellidos: String,
    val identificacion: String,  // Documento de identidad
    val fechaNacimiento: Date,
    val fechaContratacion: Date,
    val cargo: String,
    val salario: Double,
    val telefonoContacto: String,
    val direccion: String? = null,
    val email: String? = null,
    val tipoContrato: String,
    val estado: String,  // Activo, Inactivo, Vacaciones, etc.
    val permisos: NivelPermiso,
    val observaciones: String? = null,
    val fotoUrl: String? = null,
    val ultimaActualizacion: Date = Date()
)

enum class NivelPermiso {
    ADMINISTRADOR,  // Acceso completo
    SUPERVISOR,     // Puede ver y editar la mayoría de datos
    OPERARIO,       // Acceso limitado a funciones específicas
    TEMPORAL        // Acceso mínimo temporal
} 