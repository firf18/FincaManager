package com.example.fincamanager.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "cultivos")
data class Cultivo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,          // Nombre del cultivo
    val especie: String,         // Especie de planta
    val variedad: String,        // Variedad específica
    val lote: String,            // Identificación del lote o parcela
    val area: Double,            // Área plantada en hectáreas
    val fechaSiembra: Date,      // Fecha de siembra
    val fechaCosechaEstimada: Date, // Fecha estimada de cosecha
    val cantidadSemillas: Double, // Cantidad de semillas o plantas sembradas
    val unidadMedida: String,    // Kg, unidades, etc.
    val densidadSiembra: String, // Densidad de siembra (plantas por m², kg por hectárea, etc.)
    val estado: String,          // En crecimiento, Cosechado, Perdido, etc.
    val observaciones: String? = null,
    val responsable: String,
    val ultimaActualizacion: Date = Date()
) 