package com.example.fincamanager.data.model.ganado

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

/**
 * Modelo que representa un animal en el sistema de gestión ganadera.
 * 
 * Este modelo almacena toda la información básica de un animal individual,
 * como su identificación, características físicas, y datos de origen.
 */
@Entity(tableName = "animales")
data class Animal(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    // Datos de identificación
    val identificacion: String = "", // Número de identificación oficial o etiqueta
    val nombre: String = "", // Nombre opcional del animal
    
    // Datos físicos y características
    val especie: String = "", // Bovino, Ovino, Caprino, etc.
    val raza: String = "",
    val genero: String = "", // Macho, Hembra
    val fechaNacimiento: Date? = null,
    val peso: Double = 0.0, // Peso en kilogramos
    val color: String = "",
    
    // Datos de origen
    val procedencia: String = "", // Nacido en finca, Comprado, etc.
    val idMadre: String = "", // ID de la madre si es conocida
    val idPadre: String = "", // ID del padre si es conocido
    val fechaAdquisicion: Date? = null, // Fecha de compra si procede
    val precioAdquisicion: Double = 0.0, // Precio de compra si procede
    
    // Estado actual
    val estado: EstadoAnimal = EstadoAnimal.ACTIVO,
    
    // Datos de trazabilidad y sincronización
    val fechaCreacion: Date = Date(),
    val fechaActualizacion: Date = Date(),
    val sincronizado: Boolean = false
)

/**
 * Enumeración que representa los posibles estados de un animal en el sistema.
 */
enum class EstadoAnimal {
    ACTIVO,
    VENDIDO,
    FALLECIDO,
    SACRIFICADO,
    TRANSFERIDO,
    OTRO
} 