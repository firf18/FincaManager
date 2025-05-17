package com.example.fincamanager.data.repository

import com.example.fincamanager.data.model.cultivo.Cultivo
import com.example.fincamanager.data.model.cultivo.RegistroActividad
import com.example.fincamanager.data.model.cultivo.RegistroCosecha
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CultivoRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val cultivosCollection = "cultivos"
    private val actividadesCollection = "actividades_cultivo"
    private val cosechasCollection = "cosechas_cultivo"
    
    // Operaciones para Cultivos
    
    fun getCultivos(fincaId: String): Flow<List<Cultivo>> = flow {
        try {
            val snapshot = firestore.collection(cultivosCollection)
                .whereEqualTo("fincaId", fincaId)
                .get()
                .await()
            
            val cultivos = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Cultivo::class.java)
            }
            emit(cultivos)
        } catch (e: Exception) {
            emit(emptyList())
            throw e
        }
    }.flowOn(Dispatchers.IO)
    
    suspend fun getCultivoById(cultivoId: String): Cultivo? {
        return try {
            val doc = firestore.collection(cultivosCollection)
                .document(cultivoId)
                .get()
                .await()
            
            doc.toObject(Cultivo::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun saveCultivo(cultivo: Cultivo): Result<Cultivo> {
        return try {
            firestore.collection(cultivosCollection)
                .document(cultivo.id)
                .set(cultivo)
                .await()
            
            Result.success(cultivo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteCultivo(cultivoId: String): Result<Unit> {
        return try {
            firestore.collection(cultivosCollection)
                .document(cultivoId)
                .delete()
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Operaciones para Actividades
    
    fun getActividades(cultivoId: String): Flow<List<RegistroActividad>> = flow {
        try {
            val snapshot = firestore.collection(actividadesCollection)
                .whereEqualTo("cultivoId", cultivoId)
                .get()
                .await()
            
            val actividades = snapshot.documents.mapNotNull { doc ->
                doc.toObject(RegistroActividad::class.java)
            }
            emit(actividades)
        } catch (e: Exception) {
            emit(emptyList())
            throw e
        }
    }.flowOn(Dispatchers.IO)
    
    suspend fun saveActividad(actividad: RegistroActividad): Result<RegistroActividad> {
        return try {
            firestore.collection(actividadesCollection)
                .document(actividad.id)
                .set(actividad)
                .await()
            
            Result.success(actividad)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteActividad(actividadId: String): Result<Unit> {
        return try {
            firestore.collection(actividadesCollection)
                .document(actividadId)
                .delete()
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Operaciones para Cosechas
    
    fun getCosechas(cultivoId: String): Flow<List<RegistroCosecha>> = flow {
        try {
            val snapshot = firestore.collection(cosechasCollection)
                .whereEqualTo("cultivoId", cultivoId)
                .get()
                .await()
            
            val cosechas = snapshot.documents.mapNotNull { doc ->
                doc.toObject(RegistroCosecha::class.java)
            }
            emit(cosechas)
        } catch (e: Exception) {
            emit(emptyList())
            throw e
        }
    }.flowOn(Dispatchers.IO)
    
    suspend fun saveCosecha(cosecha: RegistroCosecha): Result<RegistroCosecha> {
        return try {
            firestore.collection(cosechasCollection)
                .document(cosecha.id)
                .set(cosecha)
                .await()
            
            Result.success(cosecha)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteCosecha(cosechaId: String): Result<Unit> {
        return try {
            firestore.collection(cosechasCollection)
                .document(cosechaId)
                .delete()
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 