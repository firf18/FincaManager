package com.example.fincamanager.data.local.dao

import androidx.room.*
import com.example.fincamanager.data.model.ganado.HorarioOrdenio
import com.example.fincamanager.data.model.ganado.ProduccionLeche
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * DAO (Data Access Object) para la entidad ProduccionLeche.
 * 
 * Define las operaciones de base de datos para la entidad ProduccionLeche.
 */
@Dao
interface ProduccionLecheDao {
    
    // Insertar un nuevo registro de producción de leche
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduccionLeche(produccion: ProduccionLeche): Long
    
    // Insertar múltiples registros de producción de leche
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduccionesLeche(producciones: List<ProduccionLeche>): List<Long>
    
    // Actualizar un registro de producción de leche existente
    @Update
    suspend fun updateProduccionLeche(produccion: ProduccionLeche): Int
    
    // Eliminar un registro de producción de leche
    @Delete
    suspend fun deleteProduccionLeche(produccion: ProduccionLeche): Int
    
    // Eliminar un registro de producción de leche por ID
    @Query("DELETE FROM produccion_leche WHERE id = :produccionId")
    suspend fun deleteProduccionLecheById(produccionId: String): Int
    
    // Obtener un registro de producción de leche por ID
    @Query("SELECT * FROM produccion_leche WHERE id = :produccionId")
    fun getProduccionLecheById(produccionId: String): Flow<ProduccionLeche?>
    
    // Obtener todos los registros de producción de leche de un animal
    @Query("SELECT * FROM produccion_leche WHERE animalId = :animalId ORDER BY fecha DESC")
    fun getProduccionLecheByAnimalId(animalId: String): Flow<List<ProduccionLeche>>
    
    // Obtener registros de producción de leche por rango de fechas
    @Query("SELECT * FROM produccion_leche WHERE fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha DESC")
    fun getProduccionLecheByFechaRange(fechaInicio: Date, fechaFin: Date): Flow<List<ProduccionLeche>>
    
    // Obtener registros de producción de leche por horario de ordeño
    @Query("SELECT * FROM produccion_leche WHERE horario = :horario ORDER BY fecha DESC")
    fun getProduccionLecheByHorario(horario: HorarioOrdenio): Flow<List<ProduccionLeche>>
    
    // Obtener total de leche producida por un animal en un rango de fechas
    @Query("SELECT SUM(cantidad) FROM produccion_leche WHERE animalId = :animalId AND fecha BETWEEN :fechaInicio AND :fechaFin")
    fun getTotalProduccionLecheByAnimalId(animalId: String, fechaInicio: Date, fechaFin: Date): Flow<Double?>
    
    // Clase para contener los resultados de la producción diaria
    data class ProduccionDiaria(
        val fecha: Date,
        val totalDiario: Double
    )
    
    // Obtener total de leche producida por día en un rango de fechas
    @Query("SELECT fecha, SUM(cantidad) as totalDiario FROM produccion_leche WHERE fecha BETWEEN :fechaInicio AND :fechaFin GROUP BY strftime('%Y-%m-%d', fecha/1000, 'unixepoch') ORDER BY fecha")
    fun getProduccionLecheDiaria(fechaInicio: Date, fechaFin: Date): Flow<List<ProduccionDiaria>>
    
    // Contar registros de producción de leche por horario
    @Query("SELECT COUNT(*) FROM produccion_leche WHERE horario = :horario")
    fun countProduccionLecheByHorario(horario: HorarioOrdenio): Flow<Int>
    
    // Obtener los últimos registros de producción de leche agregados
    @Query("SELECT * FROM produccion_leche ORDER BY fechaCreacion DESC LIMIT :limit")
    fun getRecentProduccionLeche(limit: Int): Flow<List<ProduccionLeche>>
    
    // Marcar registros de producción de leche como sincronizados
    @Query("UPDATE produccion_leche SET sincronizado = 1 WHERE id IN (:produccionIds)")
    suspend fun markProduccionLecheAsSincronizada(produccionIds: List<String>): Int
    
    // Obtener registros de producción de leche no sincronizados
    @Query("SELECT * FROM produccion_leche WHERE sincronizado = 0")
    fun getUnsyncedProduccionLeche(): Flow<List<ProduccionLeche>>
} 