package com.example.fincamanager.data.local.dao

import androidx.room.*
import com.example.fincamanager.data.model.ganado.RegistroSanitario
import com.example.fincamanager.data.model.ganado.TipoRegistroSanitario
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * DAO (Data Access Object) para la entidad RegistroSanitario.
 * 
 * Define las operaciones de base de datos para la entidad RegistroSanitario.
 */
@Dao
interface RegistroSanitarioDao {
    
    // Insertar un nuevo registro sanitario
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistroSanitario(registro: RegistroSanitario): Long
    
    // Insertar múltiples registros sanitarios
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistrosSanitarios(registros: List<RegistroSanitario>): List<Long>
    
    // Actualizar un registro sanitario existente
    @Update
    suspend fun updateRegistroSanitario(registro: RegistroSanitario): Int
    
    // Eliminar un registro sanitario
    @Delete
    suspend fun deleteRegistroSanitario(registro: RegistroSanitario): Int
    
    // Eliminar un registro sanitario por ID
    @Query("DELETE FROM registros_sanitarios WHERE id = :registroId")
    suspend fun deleteRegistroSanitarioById(registroId: String): Int
    
    // Obtener un registro sanitario por ID
    @Query("SELECT * FROM registros_sanitarios WHERE id = :registroId")
    fun getRegistroSanitarioById(registroId: String): Flow<RegistroSanitario?>
    
    // Obtener todos los registros sanitarios de un animal
    @Query("SELECT * FROM registros_sanitarios WHERE animalId = :animalId ORDER BY fecha DESC")
    fun getRegistrosSanitariosByAnimalId(animalId: String): Flow<List<RegistroSanitario>>
    
    // Obtener todos los registros sanitarios por tipo
    @Query("SELECT * FROM registros_sanitarios WHERE tipo = :tipo ORDER BY fecha DESC")
    fun getRegistrosSanitariosByTipo(tipo: TipoRegistroSanitario): Flow<List<RegistroSanitario>>
    
    // Obtener registros sanitarios por rango de fechas
    @Query("SELECT * FROM registros_sanitarios WHERE fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha DESC")
    fun getRegistrosSanitariosByFechaRange(fechaInicio: Date, fechaFin: Date): Flow<List<RegistroSanitario>>
    
    // Obtener registros sanitarios pendientes (con fecha de próximo tratamiento futura)
    @Query("SELECT * FROM registros_sanitarios WHERE fechaProximoTratamiento IS NOT NULL AND fechaProximoTratamiento > :fechaActual ORDER BY fechaProximoTratamiento ASC")
    fun getRegistrosSanitariosPendientes(fechaActual: Date): Flow<List<RegistroSanitario>>
    
    // Contar registros sanitarios por tipo
    @Query("SELECT COUNT(*) FROM registros_sanitarios WHERE tipo = :tipo")
    fun countRegistrosSanitariosByTipo(tipo: TipoRegistroSanitario): Flow<Int>
    
    // Obtener los últimos registros sanitarios agregados
    @Query("SELECT * FROM registros_sanitarios ORDER BY fechaCreacion DESC LIMIT :limit")
    fun getRecentRegistrosSanitarios(limit: Int): Flow<List<RegistroSanitario>>
    
    // Marcar registros sanitarios como sincronizados
    @Query("UPDATE registros_sanitarios SET sincronizado = 1 WHERE id IN (:registroIds)")
    suspend fun markRegistrosSanitariosAsSincronizados(registroIds: List<String>): Int
    
    // Obtener registros sanitarios no sincronizados
    @Query("SELECT * FROM registros_sanitarios WHERE sincronizado = 0")
    fun getUnsyncedRegistrosSanitarios(): Flow<List<RegistroSanitario>>
} 