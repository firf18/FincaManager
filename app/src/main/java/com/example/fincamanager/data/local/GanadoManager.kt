package com.example.fincamanager.data.local

import com.example.fincamanager.data.model.ganado.Animal
import com.example.fincamanager.data.model.ganado.EstadoAnimal
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que combina las operaciones del DAO y las operaciones de DataStore.
 * Proporciona una API unificada para la gestión de datos de ganado.
 */
interface GanadoManager {
    // ---- Métodos de AnimalDao ----
    suspend fun insertAnimal(animal: Animal): Long
    suspend fun insertAnimales(animales: List<Animal>): List<Long>
    suspend fun updateAnimal(animal: Animal): Int
    suspend fun deleteAnimal(animal: Animal): Int
    suspend fun deleteAnimalById(animalId: String): Int
    fun getAnimalById(animalId: String): Flow<Animal?>
    fun getAllAnimales(): Flow<List<Animal>>
    fun getAnimalesByEstado(estado: EstadoAnimal): Flow<List<Animal>>
    fun getAnimalesByEspecie(especie: String): Flow<List<Animal>>
    fun getAnimalesByEspecies(especies: Set<String>): Flow<List<Animal>>
    fun searchAnimales(query: String): Flow<List<Animal>>
    fun countAnimalesByEspecie(especie: String): Flow<Int>
    fun countAnimalesByEstado(estado: EstadoAnimal): Flow<Int>
    fun getRecentAnimales(limit: Int): Flow<List<Animal>>
    suspend fun markAnimalesAsSincronizados(animalIds: List<String>): Int
    fun getUnsyncedAnimales(): Flow<List<Animal>>
    
    // ---- Métodos de DataStore ----
    fun getEspeciesSeleccionadas(): Flow<Set<String>>
    suspend fun saveEspeciesSeleccionadas(especies: Set<String>)
} 