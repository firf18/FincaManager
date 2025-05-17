package com.example.fincamanager.data

import androidx.room.TypeConverter
import com.example.fincamanager.data.model.cultivo.Insumo
import com.example.fincamanager.data.model.cultivo.TipoActividad
import com.example.fincamanager.data.model.cultivo.EstadoCultivo
import com.example.fincamanager.data.model.cultivo.CalidadCosecha
import com.example.fincamanager.data.model.finanzas.TipoTransaccion
import com.example.fincamanager.data.model.finanzas.AreaFinca
import com.example.fincamanager.data.model.ganado.TipoEventoReproductivo
import com.example.fincamanager.data.model.ganado.TipoAnimal
import com.example.fincamanager.data.model.ganado.EstadoReproductivo
import com.example.fincamanager.data.model.ganado.Sexo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

/**
 * Clase de convertidores para Room.
 * 
 * Esta clase contiene los métodos de conversión para tipos de datos especiales
 * que Room no puede manejar directamente.
 */
class Converters {
    private val gson = Gson()
    
    // Conversores para Date
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // Conversores para List<String>
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        if (value == null) return null
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value == null) return null
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }
    
    // Conversores para List<Insumo>
    @TypeConverter
    fun fromInsumoList(value: List<Insumo>?): String? {
        if (value == null) return null
        return gson.toJson(value)
    }

    @TypeConverter
    fun toInsumoList(value: String?): List<Insumo>? {
        if (value == null) return null
        val type = object : TypeToken<List<Insumo>>() {}.type
        return gson.fromJson(value, type)
    }
    
    // Conversores para Double (por si acaso)
    @TypeConverter
    fun fromDouble(value: Double?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toDouble(value: String?): Double? {
        return value?.toDoubleOrNull()
    }
    
    // Conversores para enumerados
    @TypeConverter
    fun tipoEventoToString(tipoEvento: TipoEventoReproductivo?): String? {
        return tipoEvento?.name
    }

    @TypeConverter
    fun stringToTipoEvento(value: String?): TipoEventoReproductivo? {
        return value?.let { TipoEventoReproductivo.valueOf(it) }
    }
    
    @TypeConverter
    fun tipoAnimalToString(tipoAnimal: TipoAnimal?): String? {
        return tipoAnimal?.name
    }

    @TypeConverter
    fun stringToTipoAnimal(value: String?): TipoAnimal? {
        return value?.let { TipoAnimal.valueOf(it) }
    }
    
    @TypeConverter
    fun estadoReproductivoToString(estado: EstadoReproductivo?): String? {
        return estado?.name
    }

    @TypeConverter
    fun stringToEstadoReproductivo(value: String?): EstadoReproductivo? {
        return value?.let { EstadoReproductivo.valueOf(it) }
    }
    
    @TypeConverter
    fun sexoToString(sexo: Sexo?): String? {
        return sexo?.name
    }

    @TypeConverter
    fun stringToSexo(value: String?): Sexo? {
        return value?.let { Sexo.valueOf(it) }
    }
    
    // Conversores para enumerados de cultivo
    @TypeConverter
    fun tipoActividadToString(tipoActividad: TipoActividad?): String? {
        return tipoActividad?.name
    }

    @TypeConverter
    fun stringToTipoActividad(value: String?): TipoActividad? {
        return value?.let { TipoActividad.valueOf(it) }
    }
    
    @TypeConverter
    fun estadoCultivoToString(estado: EstadoCultivo?): String? {
        return estado?.name
    }

    @TypeConverter
    fun stringToEstadoCultivo(value: String?): EstadoCultivo? {
        return value?.let { EstadoCultivo.valueOf(it) }
    }
    
    @TypeConverter
    fun calidadCosechaToString(calidad: CalidadCosecha?): String? {
        return calidad?.name
    }

    @TypeConverter
    fun stringToCalidadCosecha(value: String?): CalidadCosecha? {
        return value?.let { CalidadCosecha.valueOf(it) }
    }
    
    // Conversores para enumerados de finanzas
    @TypeConverter
    fun tipoTransaccionToString(tipo: TipoTransaccion?): String? {
        return tipo?.name
    }

    @TypeConverter
    fun stringToTipoTransaccion(value: String?): TipoTransaccion? {
        return value?.let { TipoTransaccion.valueOf(it) }
    }
    
    @TypeConverter
    fun areaFincaToString(area: AreaFinca?): String? {
        return area?.name
    }

    @TypeConverter
    fun stringToAreaFinca(value: String?): AreaFinca? {
        return value?.let { AreaFinca.valueOf(it) }
    }
} 