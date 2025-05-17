package com.example.fincamanager.data.repository

import com.example.fincamanager.data.local.GanadoManager
import com.example.fincamanager.data.local.dao.ProduccionLecheDao
import com.example.fincamanager.data.local.dao.RegistroReproduccionDao
import com.example.fincamanager.data.local.dao.RegistroSanitarioDao
import com.example.fincamanager.data.model.ganado.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para la gestión de datos relacionados con el ganado.
 *
 * Este repositorio coordina el acceso a los datos entre la base de datos local (Room)
 * y la nube (Firebase Firestore), proporcionando una interfaz unificada para las operaciones
 * relacionadas con la gestión ganadera.
 */
@Singleton
class GanadoRepository @Inject constructor(
    private val ganadoManager: GanadoManager,
    private val registroSanitarioDao: RegistroSanitarioDao,
    private val produccionLecheDao: ProduccionLecheDao,
    private val registroReproduccionDao: RegistroReproduccionDao,
    private val firestore: FirebaseFirestore
) {
    // Colecciones en Firestore
    private val animalesCollection = firestore.collection("animales")
    private val registrosSanitariosCollection = firestore.collection("registros_sanitarios")
    private val produccionLecheCollection = firestore.collection("produccion_leche")
    private val registrosReproduccionCollection = firestore.collection("registros_reproduccion")

    // ---- Métodos para Animal ----

    // Obtener todos los animales
    fun getAllAnimales(): Flow<List<Animal>> = ganadoManager.getAllAnimales()

    // Obtener animal por ID
    fun getAnimalById(animalId: String): Flow<Animal?> = ganadoManager.getAnimalById(animalId)

    // Obtener animales por estado
    fun getAnimalesByEstado(estado: EstadoAnimal): Flow<List<Animal>> = ganadoManager.getAnimalesByEstado(estado)

    // Obtener animales por especie
    fun getAnimalesByEspecie(especie: String): Flow<List<Animal>> = ganadoManager.getAnimalesByEspecie(especie)

    // Buscar animales
    fun searchAnimales(query: String): Flow<List<Animal>> = ganadoManager.searchAnimales(query)

    // Contar animales por especie
    fun countAnimalesByEspecie(especie: String): Flow<Int> = ganadoManager.countAnimalesByEspecie(especie)

    // Contar animales por estado
    fun countAnimalesByEstado(estado: EstadoAnimal): Flow<Int> = ganadoManager.countAnimalesByEstado(estado)

    // Guardar un animal (local y en la nube)
    suspend fun saveAnimal(animal: Animal): String {
        // Guardar en la base de datos local
        ganadoManager.insertAnimal(animal)

        // Guardar en Firestore
        try {
            animalesCollection.document(animal.id).set(animal).await()
            // Marcar como sincronizado
            ganadoManager.markAnimalesAsSincronizados(listOf(animal.id))
        } catch (e: Exception) {
            // Manejar error de sincronización
            // En caso de error, el animal queda guardado localmente pero no sincronizado
        }

        return animal.id
    }

    // Actualizar un animal (local y en la nube)
    suspend fun updateAnimal(animal: Animal): Boolean {
        // Preparar la versión actualizada con la fecha de actualización actual
        val updatedAnimal = animal.copy(
            fechaActualizacion = Date(),
            sincronizado = false
        )

        // Actualizar en la base de datos local
        val rowsAffected = ganadoManager.updateAnimal(updatedAnimal)

        // Actualizar en Firestore
        try {
            animalesCollection.document(updatedAnimal.id).set(updatedAnimal).await()
            // Marcar como sincronizado
            ganadoManager.markAnimalesAsSincronizados(listOf(updatedAnimal.id))
        } catch (e: Exception) {
            // Manejar error de sincronización
        }

        return rowsAffected > 0
    }

    // Eliminar un animal (local y en la nube)
    suspend fun deleteAnimal(animalId: String): Boolean {
        // Eliminar de la base de datos local
        val rowsAffected = ganadoManager.deleteAnimalById(animalId)

        // Eliminar de Firestore
        try {
            animalesCollection.document(animalId).delete().await()
        } catch (e: Exception) {
            // Manejar error de sincronización
        }

        return rowsAffected > 0
    }

    // ---- Métodos para RegistroSanitario ----

    // Obtener registros sanitarios por animal
    fun getRegistrosSanitariosByAnimalId(animalId: String): Flow<List<RegistroSanitario>> = 
        registroSanitarioDao.getRegistrosSanitariosByAnimalId(animalId)

    // Guardar un registro sanitario (local y en la nube)
    suspend fun saveRegistroSanitario(registro: RegistroSanitario): String {
        // Guardar en la base de datos local
        registroSanitarioDao.insertRegistroSanitario(registro)

        // Guardar en Firestore
        try {
            registrosSanitariosCollection.document(registro.id).set(registro).await()
            // Marcar como sincronizado
            registroSanitarioDao.markRegistrosSanitariosAsSincronizados(listOf(registro.id))
        } catch (e: Exception) {
            // Manejar error de sincronización
        }

        return registro.id
    }

    // Actualizar un registro sanitario (local y en la nube)
    suspend fun updateRegistroSanitario(registro: RegistroSanitario): Boolean {
        // Preparar la versión actualizada
        val updatedRegistro = registro.copy(
            fechaActualizacion = Date(),
            sincronizado = false
        )

        // Actualizar en la base de datos local
        val rowsAffected = registroSanitarioDao.updateRegistroSanitario(updatedRegistro)

        // Actualizar en Firestore
        try {
            registrosSanitariosCollection.document(updatedRegistro.id).set(updatedRegistro).await()
            // Marcar como sincronizado
            registroSanitarioDao.markRegistrosSanitariosAsSincronizados(listOf(updatedRegistro.id))
        } catch (e: Exception) {
            // Manejar error de sincronización
        }

        return rowsAffected > 0
    }

    // Eliminar un registro sanitario (local y en la nube)
    suspend fun deleteRegistroSanitario(registroId: String): Boolean {
        // Eliminar de la base de datos local
        val rowsAffected = registroSanitarioDao.deleteRegistroSanitarioById(registroId)

        // Eliminar de Firestore
        try {
            registrosSanitariosCollection.document(registroId).delete().await()
        } catch (e: Exception) {
            // Manejar error de sincronización
        }

        return rowsAffected > 0
    }

    // ---- Métodos para ProduccionLeche ----

    // Obtener registros de producción de leche por animal
    fun getProduccionLecheByAnimalId(animalId: String): Flow<List<ProduccionLeche>> = 
        produccionLecheDao.getProduccionLecheByAnimalId(animalId)

    // Obtener total de producción de leche por animal en un rango de fechas
    fun getTotalProduccionLecheByAnimalId(animalId: String, fechaInicio: Date, fechaFin: Date): Flow<Double?> = 
        produccionLecheDao.getTotalProduccionLecheByAnimalId(animalId, fechaInicio, fechaFin)

    // Guardar un registro de producción de leche (local y en la nube)
    suspend fun saveProduccionLeche(produccion: ProduccionLeche): String {
        // Guardar en la base de datos local
        produccionLecheDao.insertProduccionLeche(produccion)

        // Guardar en Firestore
        try {
            produccionLecheCollection.document(produccion.id).set(produccion).await()
            // Marcar como sincronizado
            produccionLecheDao.markProduccionLecheAsSincronizada(listOf(produccion.id))
        } catch (e: Exception) {
            // Manejar error de sincronización
        }

        return produccion.id
    }

    // Actualizar un registro de producción de leche (local y en la nube)
    suspend fun updateProduccionLeche(produccion: ProduccionLeche): Boolean {
        // Preparar la versión actualizada
        val updatedProduccion = produccion.copy(
            fechaActualizacion = Date(),
            sincronizado = false
        )

        // Actualizar en la base de datos local
        val rowsAffected = produccionLecheDao.updateProduccionLeche(updatedProduccion)

        // Actualizar en Firestore
        try {
            produccionLecheCollection.document(updatedProduccion.id).set(updatedProduccion).await()
            // Marcar como sincronizado
            produccionLecheDao.markProduccionLecheAsSincronizada(listOf(updatedProduccion.id))
        } catch (e: Exception) {
            // Manejar error de sincronización
        }

        return rowsAffected > 0
    }

    // Eliminar un registro de producción de leche (local y en la nube)
    suspend fun deleteProduccionLeche(produccionId: String): Boolean {
        // Eliminar de la base de datos local
        val rowsAffected = produccionLecheDao.deleteProduccionLecheById(produccionId)

        // Eliminar de Firestore
        try {
            produccionLecheCollection.document(produccionId).delete().await()
        } catch (e: Exception) {
            // Manejar error de sincronización
        }

        return rowsAffected > 0
    }

    // ---- Métodos para RegistroReproduccion ----

    // Obtener registros reproductivos por animal
    fun getRegistrosReproduccionByAnimalId(animalId: String): Flow<List<RegistroReproduccion>> = 
        registroReproduccionDao.getRegistrosReproduccionByAnimalId(animalId)

    // Obtener próximos partos
    fun getProximosPartos(fechaActual: Date): Flow<List<RegistroReproduccion>> = 
        registroReproduccionDao.getProximosPartos(fechaActual)

    // Guardar un registro reproductivo (local y en la nube)
    suspend fun saveRegistroReproduccion(registro: RegistroReproduccion): String {
        // Guardar en la base de datos local
        registroReproduccionDao.insertRegistroReproduccion(registro)

        // Guardar en Firestore
        try {
            registrosReproduccionCollection.document(registro.id).set(registro).await()
            // Marcar como sincronizado
            registroReproduccionDao.markRegistrosReproduccionAsSincronizados(listOf(registro.id))
        } catch (e: Exception) {
            // Manejar error de sincronización
        }

        return registro.id
    }

    // Actualizar un registro reproductivo (local y en la nube)
    suspend fun updateRegistroReproduccion(registro: RegistroReproduccion): Boolean {
        // Preparar la versión actualizada
        val updatedRegistro = registro.copy(
            fechaActualizacion = Date(),
            sincronizado = false
        )

        // Actualizar en la base de datos local
        val rowsAffected = registroReproduccionDao.updateRegistroReproduccion(updatedRegistro)

        // Actualizar en Firestore
        try {
            registrosReproduccionCollection.document(updatedRegistro.id).set(updatedRegistro).await()
            // Marcar como sincronizado
            registroReproduccionDao.markRegistrosReproduccionAsSincronizados(listOf(updatedRegistro.id))
        } catch (e: Exception) {
            // Manejar error de sincronización
        }

        return rowsAffected > 0
    }

    // Eliminar un registro reproductivo (local y en la nube)
    suspend fun deleteRegistroReproduccion(registroId: String): Boolean {
        // Eliminar de la base de datos local
        val rowsAffected = registroReproduccionDao.deleteRegistroReproduccionById(registroId)

        // Eliminar de Firestore
        try {
            registrosReproduccionCollection.document(registroId).delete().await()
        } catch (e: Exception) {
            // Manejar error de sincronización
        }

        return rowsAffected > 0
    }

    // ---- Métodos para gestión de especies seleccionadas ----

    // Obtener especies seleccionadas desde DataStore
    fun getEspeciesSeleccionadas(): Flow<Set<String>> {
        return ganadoManager.getEspeciesSeleccionadas()
    }

    // Guardar especies seleccionadas en DataStore
    suspend fun saveEspeciesSeleccionadas(especies: Set<String>) {
        ganadoManager.saveEspeciesSeleccionadas(especies)
    }

    // Obtener animales filtrados por especies
    fun getAnimalesFiltrados(especies: Set<String>): Flow<List<Animal>> {
        return if (especies.isEmpty()) {
            getAllAnimales()
        } else {
            ganadoManager.getAnimalesByEspecies(especies)
        }
    }

    // ---- Métodos de sincronización ----

    // Sincronizar datos locales con Firestore
    suspend fun syncLocalDataToFirestore() {
        // Sincronizar animales no sincronizados
        ganadoManager.getUnsyncedAnimales().collect { animales ->
            for (animal in animales) {
                try {
                    animalesCollection.document(animal.id).set(animal).await()
                    ganadoManager.markAnimalesAsSincronizados(listOf(animal.id))
                } catch (e: Exception) {
                    // Manejar error de sincronización
                }
            }
        }

        // Sincronizar registros sanitarios no sincronizados
        registroSanitarioDao.getUnsyncedRegistrosSanitarios().collect { registros ->
            for (registro in registros) {
                try {
                    registrosSanitariosCollection.document(registro.id).set(registro).await()
                    registroSanitarioDao.markRegistrosSanitariosAsSincronizados(listOf(registro.id))
                } catch (e: Exception) {
                    // Manejar error de sincronización
                }
            }
        }

        // Sincronizar registros de producción de leche no sincronizados
        produccionLecheDao.getUnsyncedProduccionLeche().collect { producciones ->
            for (produccion in producciones) {
                try {
                    produccionLecheCollection.document(produccion.id).set(produccion).await()
                    produccionLecheDao.markProduccionLecheAsSincronizada(listOf(produccion.id))
                } catch (e: Exception) {
                    // Manejar error de sincronización
                }
            }
        }

        // Sincronizar registros reproductivos no sincronizados
        registroReproduccionDao.getUnsyncedRegistrosReproduccion().collect { registros ->
            for (registro in registros) {
                try {
                    registrosReproduccionCollection.document(registro.id).set(registro).await()
                    registroReproduccionDao.markRegistrosReproduccionAsSincronizados(listOf(registro.id))
                } catch (e: Exception) {
                    // Manejar error de sincronización
                }
            }
        }
    }
} 