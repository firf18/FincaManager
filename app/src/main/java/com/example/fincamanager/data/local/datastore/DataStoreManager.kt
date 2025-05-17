package com.example.fincamanager.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extensión para acceder a DataStore desde el contexto
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "finca_manager_preferences"
)

/**
 * Gestor de DataStore para persistencia de preferencias de la aplicación.
 */
@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        // Keys para DataStore
        private val ESPECIES_SELECCIONADAS = stringPreferencesKey("especies_seleccionadas")
    }

    /**
     * Obtiene las especies de animales seleccionadas almacenadas en DataStore.
     * @return Flow con el conjunto de especies seleccionadas.
     */
    fun getEspeciesSeleccionadas(): Flow<Set<String>> {
        return context.dataStore.data.map { preferences ->
            // Obtener string de preferencias y convertir a Set (o retornar vacío si no existe)
            val especiesString = preferences[ESPECIES_SELECCIONADAS] ?: ""
            if (especiesString.isEmpty()) {
                emptySet()
            } else {
                especiesString.split(",").toSet()
            }
        }
    }

    /**
     * Guarda las especies de animales seleccionadas en DataStore.
     * @param especies Conjunto de especies a guardar.
     */
    suspend fun saveEspeciesSeleccionadas(especies: Set<String>) {
        context.dataStore.edit { preferences ->
            // Convertir Set a string separado por comas para almacenamiento
            preferences[ESPECIES_SELECCIONADAS] = especies.joinToString(",")
        }
    }
} 