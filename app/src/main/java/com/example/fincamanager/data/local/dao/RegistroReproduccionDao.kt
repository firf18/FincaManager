package com.example.fincamanager.data.local.dao

import androidx.room.*
import com.example.fincamanager.data.model.ganado.RegistroReproduccion
import com.example.fincamanager.data.model.ganado.TipoEventoReproductivo
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * DAO (Data Access Object) para la entidad RegistroReproduccion.
 * 
 * Define las operaciones de base de datos para la entidad RegistroReproduccion.
 */
@Dao
interface RegistroReproduccionDao {
    
    // Insertar un nuevo registro reproductivo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistroReproduccion(registro: RegistroReproduccion): Long
    
    // Insertar múltiples registros reproductivos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistrosReproduccion(registros: List<RegistroReproduccion>): List<Long>
    
    // Actualizar un registro reproductivo existente
    @Update
    suspend fun updateRegistroReproduccion(registro: RegistroReproduccion): Int
    
    // Eliminar un registro reproductivo
    @Delete
    suspend fun deleteRegistroReproduccion(registro: RegistroReproduccion): Int
    
    // Eliminar un registro reproductivo por ID
    @Query("DELETE FROM registros_reproduccion WHERE id = :registroId")
    suspend fun deleteRegistroReproduccionById(registroId: String): Int
    
    // Obtener un registro reproductivo por ID
    @Query("SELECT * FROM registros_reproduccion WHERE id = :registroId")
    fun getRegistroReproduccionById(registroId: String): Flow<RegistroReproduccion?>
    
    // Obtener todos los registros reproductivos de un animal
    @Query("SELECT * FROM registros_reproduccion WHERE animalId = :animalId ORDER BY fecha DESC")
    fun getRegistrosReproduccionByAnimalId(animalId: String): Flow<List<RegistroReproduccion>>
    
    // Obtener registros reproductivos por tipo de evento
    @Query("SELECT * FROM registros_reproduccion WHERE tipoEvento = :tipoEvento ORDER BY fecha DESC")
    fun getRegistrosReproduccionByTipoEvento(tipoEvento: TipoEventoReproductivo): Flow<List<RegistroReproduccion>>
    
    // Obtener registros reproductivos por rango de fechas
    @Query("SELECT * FROM registros_reproduccion WHERE fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha DESC")
    fun getRegistrosReproduccionByFechaRange(fechaInicio: Date, fechaFin: Date): Flow<List<RegistroReproduccion>>
    
    // Obtener registros de partos que ocurrieron en un rango de fechas
    @Query("SELECT * FROM registros_reproduccion WHERE tipoEvento = 'PARTO' AND fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha DESC")
    fun getPartosByFechaRange(fechaInicio: Date, fechaFin: Date): Flow<List<RegistroReproduccion>>
    
    // Obtener próximos partos (basado en fechaProbableParto)
    @Query("SELECT * FROM registros_reproduccion WHERE tipoEvento = 'DIAGNOSTICO_GESTACION' AND fechaProbableParto IS NOT NULL AND fechaProbableParto > :fechaActual ORDER BY fechaProbableParto ASC")
    fun getProximosPartos(fechaActual: Date): Flow<List<RegistroReproduccion>>
    
    // Contar registros reproductivos por tipo de evento
    @Query("SELECT COUNT(*) FROM registros_reproduccion WHERE tipoEvento = :tipoEvento")
    fun countRegistrosReproduccionByTipoEvento(tipoEvento: TipoEventoReproductivo): Flow<Int>
    
    // Obtener los últimos registros reproductivos agregados
    @Query("SELECT * FROM registros_reproduccion ORDER BY fechaCreacion DESC LIMIT :limit")
    fun getRecentRegistrosReproduccion(limit: Int): Flow<List<RegistroReproduccion>>
    
    // Marcar registros reproductivos como sincronizados
    @Query("UPDATE registros_reproduccion SET sincronizado = 1 WHERE id IN (:registroIds)")
    suspend fun markRegistrosReproduccionAsSincronizados(registroIds: List<String>): Int
    
    // Obtener registros reproductivos no sincronizados
    @Query("SELECT * FROM registros_reproduccion WHERE sincronizado = 0")
    fun getUnsyncedRegistrosReproduccion(): Flow<List<RegistroReproduccion>>
    
    // Obtener todas las crías registradas de un animal específico (como madre)
    @Query("SELECT * FROM registros_reproduccion WHERE tipoEvento = 'PARTO' AND animalId = :madreId")
    fun getCriasByMadreId(madreId: String): Flow<List<RegistroReproduccion>>
} 