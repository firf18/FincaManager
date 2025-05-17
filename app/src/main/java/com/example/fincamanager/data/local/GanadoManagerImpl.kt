package com.example.fincamanager.data.local

import com.example.fincamanager.data.local.dao.AnimalDao
import com.example.fincamanager.data.local.datastore.DataStoreManager
import com.example.fincamanager.data.model.ganado.Animal
import com.example.fincamanager.data.model.ganado.EstadoAnimal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de GanadoManager que combina AnimalDao y DataStoreManager
 * para proporcionar una API unificada para la gestión de datos de ganado.
 */
@Singleton
class GanadoManagerImpl @Inject constructor(
    private val animalDao: AnimalDao,
    private val dataStoreManager: DataStoreManager
) : GanadoManager {
    // ---- Delegación de métodos de AnimalDao ----
    override suspend fun insertAnimal(animal: Animal): Long = animalDao.insertAnimal(animal)
    
    override suspend fun insertAnimales(animales: List<Animal>): List<Long> = animalDao.insertAnimales(animales)
    
    override suspend fun updateAnimal(animal: Animal): Int = animalDao.updateAnimal(animal)
    
    override suspend fun deleteAnimal(animal: Animal): Int = animalDao.deleteAnimal(animal)
    
    override suspend fun deleteAnimalById(animalId: String): Int = animalDao.deleteAnimalById(animalId)
    
    override fun getAnimalById(animalId: String): Flow<Animal?> = animalDao.getAnimalById(animalId)
    
    override fun getAllAnimales(): Flow<List<Animal>> = animalDao.getAllAnimales()
    
    override fun getAnimalesByEstado(estado: EstadoAnimal): Flow<List<Animal>> = 
        animalDao.getAnimalesByEstado(estado)
    
    override fun getAnimalesByEspecie(especie: String): Flow<List<Animal>> = 
        animalDao.getAnimalesByEspecie(especie)
    
    override fun getAnimalesByEspecies(especies: Set<String>): Flow<List<Animal>> = 
        animalDao.getAnimalesByEspecies(especies)
    
    override fun searchAnimales(query: String): Flow<List<Animal>> = animalDao.searchAnimales(query)
    
    override fun countAnimalesByEspecie(especie: String): Flow<Int> = animalDao.countAnimalesByEspecie(especie)
    
    override fun countAnimalesByEstado(estado: EstadoAnimal): Flow<Int> = 
        animalDao.countAnimalesByEstado(estado)
    
    override fun getRecentAnimales(limit: Int): Flow<List<Animal>> = animalDao.getRecentAnimales(limit)
    
    override suspend fun markAnimalesAsSincronizados(animalIds: List<String>): Int = 
        animalDao.markAnimalesAsSincronizados(animalIds)
    
    override fun getUnsyncedAnimales(): Flow<List<Animal>> = animalDao.getUnsyncedAnimales()
    
    // ---- Delegación de métodos de DataStore ----
    override fun getEspeciesSeleccionadas(): Flow<Set<String>> = 
        dataStoreManager.getEspeciesSeleccionadas()
    
    override suspend fun saveEspeciesSeleccionadas(especies: Set<String>) {
        dataStoreManager.saveEspeciesSeleccionadas(especies)
    }
} 