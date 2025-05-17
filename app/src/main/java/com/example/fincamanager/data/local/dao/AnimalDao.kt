package com.example.fincamanager.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fincamanager.data.model.ganado.Animal
import com.example.fincamanager.data.model.ganado.EstadoAnimal
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para la entidad Animal.
 * 
 * Define las operaciones de base de datos para la entidad Animal.
 */
@Dao
interface AnimalDao {
    
    // Insertar un nuevo animal
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimal(animal: Animal): Long
    
    // Insertar múltiples animales
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimales(animales: List<Animal>): List<Long>
    
    // Actualizar un animal existente
    @Update
    suspend fun updateAnimal(animal: Animal): Int
    
    // Eliminar un animal
    @Delete
    suspend fun deleteAnimal(animal: Animal): Int
    
    // Eliminar un animal por ID
    @Query("DELETE FROM animales WHERE id = :animalId")
    suspend fun deleteAnimalById(animalId: String): Int
    
    // Obtener un animal por ID
    @Query("SELECT * FROM animales WHERE id = :animalId")
    fun getAnimalById(animalId: String): Flow<Animal?>
    
    // Obtener todos los animales
    @Query("SELECT * FROM animales ORDER BY fechaActualizacion DESC")
    fun getAllAnimales(): Flow<List<Animal>>
    
    // Obtener animales por estado
    @Query("SELECT * FROM animales WHERE estado = :estado ORDER BY fechaActualizacion DESC")
    fun getAnimalesByEstado(estado: EstadoAnimal): Flow<List<Animal>>
    
    // Obtener animales por especie
    @Query("SELECT * FROM animales WHERE especie = :especie ORDER BY fechaActualizacion DESC")
    fun getAnimalesByEspecie(especie: String): Flow<List<Animal>>
    
    // Obtener animales por lista de especies
    @Query("SELECT * FROM animales WHERE especie IN (:especies) ORDER BY fechaActualizacion DESC")
    fun getAnimalesByEspecies(especies: Set<String>): Flow<List<Animal>>
    
    // Buscar animales por nombre o identificación
    @Query("SELECT * FROM animales WHERE nombre LIKE '%' || :query || '%' OR identificacion LIKE '%' || :query || '%' ORDER BY fechaActualizacion DESC")
    fun searchAnimales(query: String): Flow<List<Animal>>
    
    // Contar animales por especie
    @Query("SELECT COUNT(*) FROM animales WHERE especie = :especie")
    fun countAnimalesByEspecie(especie: String): Flow<Int>
    
    // Contar animales por estado
    @Query("SELECT COUNT(*) FROM animales WHERE estado = :estado")
    fun countAnimalesByEstado(estado: EstadoAnimal): Flow<Int>
    
    // Obtener los últimos animales agregados
    @Query("SELECT * FROM animales ORDER BY fechaCreacion DESC LIMIT :limit")
    fun getRecentAnimales(limit: Int): Flow<List<Animal>>
    
    // Marcar animales como sincronizados
    @Query("UPDATE animales SET sincronizado = 1 WHERE id IN (:animalIds)")
    suspend fun markAnimalesAsSincronizados(animalIds: List<String>): Int
    
    // Obtener animales no sincronizados
    @Query("SELECT * FROM animales WHERE sincronizado = 0")
    fun getUnsyncedAnimales(): Flow<List<Animal>>
} 