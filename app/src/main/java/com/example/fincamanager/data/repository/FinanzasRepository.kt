package com.example.fincamanager.data.repository

import com.example.fincamanager.data.model.finanzas.Presupuesto
import com.example.fincamanager.data.model.finanzas.Transaccion
import com.example.fincamanager.data.model.finanzas.TipoTransaccion
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanzasRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val transaccionesCollection = "transacciones"
    private val presupuestosCollection = "presupuestos"
    
    // Operaciones para Transacciones
    
    fun getTransacciones(fincaId: String): Flow<List<Transaccion>> = flow {
        try {
            val snapshot = firestore.collection(transaccionesCollection)
                .whereEqualTo("fincaId", fincaId)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val transacciones = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaccion::class.java)
            }
            emit(transacciones)
        } catch (e: Exception) {
            emit(emptyList())
            throw e
        }
    }.flowOn(Dispatchers.IO)
    
    fun getTransaccionesPorFecha(fincaId: String, fechaInicio: Date, fechaFin: Date): Flow<List<Transaccion>> = flow {
        try {
            val snapshot = firestore.collection(transaccionesCollection)
                .whereEqualTo("fincaId", fincaId)
                .whereGreaterThanOrEqualTo("fecha", fechaInicio)
                .whereLessThanOrEqualTo("fecha", fechaFin)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val transacciones = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaccion::class.java)
            }
            emit(transacciones)
        } catch (e: Exception) {
            emit(emptyList())
            throw e
        }
    }.flowOn(Dispatchers.IO)
    
    fun getTransaccionesPorArea(fincaId: String, area: String): Flow<List<Transaccion>> = flow {
        try {
            val snapshot = firestore.collection(transaccionesCollection)
                .whereEqualTo("fincaId", fincaId)
                .whereEqualTo("area", area)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val transacciones = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaccion::class.java)
            }
            emit(transacciones)
        } catch (e: Exception) {
            emit(emptyList())
            throw e
        }
    }.flowOn(Dispatchers.IO)
    
    fun getTransaccionesPorTipo(fincaId: String, tipo: TipoTransaccion): Flow<List<Transaccion>> = flow {
        try {
            val snapshot = firestore.collection(transaccionesCollection)
                .whereEqualTo("fincaId", fincaId)
                .whereEqualTo("tipo", tipo)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val transacciones = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaccion::class.java)
            }
            emit(transacciones)
        } catch (e: Exception) {
            emit(emptyList())
            throw e
        }
    }.flowOn(Dispatchers.IO)
    
    suspend fun saveTransaccion(transaccion: Transaccion): Result<Transaccion> {
        return try {
            firestore.collection(transaccionesCollection)
                .document(transaccion.id)
                .set(transaccion)
                .await()
            
            Result.success(transaccion)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteTransaccion(transaccionId: String): Result<Unit> {
        return try {
            firestore.collection(transaccionesCollection)
                .document(transaccionId)
                .delete()
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Operaciones para Presupuestos
    
    fun getPresupuestos(fincaId: String): Flow<List<Presupuesto>> = flow {
        try {
            val snapshot = firestore.collection(presupuestosCollection)
                .whereEqualTo("fincaId", fincaId)
                .whereEqualTo("activo", true)
                .get()
                .await()
            
            val presupuestos = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Presupuesto::class.java)
            }
            emit(presupuestos)
        } catch (e: Exception) {
            emit(emptyList())
            throw e
        }
    }.flowOn(Dispatchers.IO)
    
    fun getPresupuestosPorArea(fincaId: String, area: String): Flow<List<Presupuesto>> = flow {
        try {
            val snapshot = firestore.collection(presupuestosCollection)
                .whereEqualTo("fincaId", fincaId)
                .whereEqualTo("area", area)
                .whereEqualTo("activo", true)
                .get()
                .await()
            
            val presupuestos = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Presupuesto::class.java)
            }
            emit(presupuestos)
        } catch (e: Exception) {
            emit(emptyList())
            throw e
        }
    }.flowOn(Dispatchers.IO)
    
    suspend fun savePresupuesto(presupuesto: Presupuesto): Result<Presupuesto> {
        return try {
            firestore.collection(presupuestosCollection)
                .document(presupuesto.id)
                .set(presupuesto)
                .await()
            
            Result.success(presupuesto)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deletePresupuesto(presupuestoId: String): Result<Unit> {
        return try {
            firestore.collection(presupuestosCollection)
                .document(presupuestoId)
                .delete()
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 