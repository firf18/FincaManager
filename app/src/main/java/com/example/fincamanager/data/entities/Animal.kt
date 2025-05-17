package com.example.fincamanager.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "animales")
data class Animal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val identificacion: String,  // Número o ID único del animal
    val tipo: String,           // Tipo de animal (vaca, toro, oveja, etc.)
    val raza: String,           // Raza del animal
    val fechaNacimiento: Date,  // Fecha de nacimiento
    val sexo: String,           // Macho, Hembra
    val pesoInicial: Double,    // Peso inicial en kg
    val pesoActual: Double,     // Peso actual en kg
    val madre: String? = null,  // Identificación de la madre, si se conoce
    val padre: String? = null,  // Identificación del padre, si se conoce
    val fechaIngreso: Date,     // Fecha en que ingresó a la finca
    val estado: String,         // Activo, Vendido, Fallecido, etc.
    val observaciones: String? = null,   // Observaciones generales
    val imagenUrl: String? = null,       // URL o path de la imagen del animal
    val ultimaActualizacion: Date = Date()  // Fecha de última actualización del registro
) 