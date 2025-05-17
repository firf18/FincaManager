package com.example.fincamanager.ui.cultivo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fincamanager.data.model.cultivo.Cultivo
import com.example.fincamanager.data.model.cultivo.RegistroActividad
import com.example.fincamanager.data.model.cultivo.RegistroCosecha
import com.example.fincamanager.data.repository.CultivoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CultivoViewModel @Inject constructor(
    private val cultivoRepository: CultivoRepository
) : ViewModel() {

    // Estado para la lista de cultivos
    private val _cultivosState = MutableStateFlow<ListaCultivosState>(ListaCultivosState.Loading)
    val cultivosState: StateFlow<ListaCultivosState> = _cultivosState
    
    // Estado para un cultivo seleccionado
    private val _cultivoSeleccionado = MutableStateFlow<Cultivo?>(null)
    val cultivoSeleccionado: StateFlow<Cultivo?> = _cultivoSeleccionado
    
    // Estado para la lista de actividades de un cultivo
    private val _actividadesState = MutableStateFlow<ListaActividadesState>(ListaActividadesState.Loading)
    val actividadesState: StateFlow<ListaActividadesState> = _actividadesState
    
    // Estado para la lista de cosechas de un cultivo
    private val _cosechasState = MutableStateFlow<ListaCosechasState>(ListaCosechasState.Loading)
    val cosechasState: StateFlow<ListaCosechasState> = _cosechasState
    
    // Mensaje de operación para retroalimentación al usuario
    private val _mensajeOperacion = MutableStateFlow<String?>(null)
    val mensajeOperacion: StateFlow<String?> = _mensajeOperacion
    
    // Estados sellados para manejar los diferentes estados de la UI
    sealed class ListaCultivosState {
        object Loading : ListaCultivosState()
        data class Success(val cultivos: List<Cultivo>) : ListaCultivosState()
        data class Error(val mensaje: String) : ListaCultivosState()
    }
    
    sealed class ListaActividadesState {
        object Loading : ListaActividadesState()
        data class Success(val actividades: List<RegistroActividad>) : ListaActividadesState()
        data class Error(val mensaje: String) : ListaActividadesState()
    }
    
    sealed class ListaCosechasState {
        object Loading : ListaCosechasState()
        data class Success(val cosechas: List<RegistroCosecha>) : ListaCosechasState()
        data class Error(val mensaje: String) : ListaCosechasState()
    }
    
    // Cargar cultivos de una finca específica
    fun cargarCultivos(fincaId: String) {
        viewModelScope.launch {
            _cultivosState.value = ListaCultivosState.Loading
            
            cultivoRepository.getCultivos(fincaId)
                .catch { e ->
                    _cultivosState.value = ListaCultivosState.Error("Error al cargar cultivos: ${e.message}")
                }
                .collect { cultivos ->
                    _cultivosState.value = ListaCultivosState.Success(cultivos)
                }
        }
    }
    
    // Cargar un cultivo específico por su ID
    fun cargarCultivo(cultivoId: String) {
        viewModelScope.launch {
            val cultivo = cultivoRepository.getCultivoById(cultivoId)
            _cultivoSeleccionado.value = cultivo
        }
    }
    
    // Guardar o actualizar un cultivo
    fun guardarCultivo(cultivo: Cultivo) {
        viewModelScope.launch {
            val resultado = cultivoRepository.saveCultivo(cultivo)
            
            if (resultado.isSuccess) {
                _mensajeOperacion.value = "Cultivo guardado correctamente"
                // Actualizar el cultivo seleccionado si es el mismo
                _cultivoSeleccionado.value = cultivo
            } else {
                _mensajeOperacion.value = "Error al guardar el cultivo: ${resultado.exceptionOrNull()?.message}"
            }
        }
    }
    
    // Eliminar un cultivo
    fun eliminarCultivo(cultivoId: String) {
        viewModelScope.launch {
            val resultado = cultivoRepository.deleteCultivo(cultivoId)
            
            if (resultado.isSuccess) {
                _mensajeOperacion.value = "Cultivo eliminado correctamente"
                // Si el cultivo eliminado es el seleccionado, limpiamos la selección
                if (_cultivoSeleccionado.value?.id == cultivoId) {
                    _cultivoSeleccionado.value = null
                }
            } else {
                _mensajeOperacion.value = "Error al eliminar el cultivo: ${resultado.exceptionOrNull()?.message}"
            }
        }
    }
    
    // Cargar actividades de un cultivo específico
    fun cargarActividades(cultivoId: String) {
        viewModelScope.launch {
            _actividadesState.value = ListaActividadesState.Loading
            
            cultivoRepository.getActividades(cultivoId)
                .catch { e ->
                    _actividadesState.value = ListaActividadesState.Error("Error al cargar actividades: ${e.message}")
                }
                .collect { actividades ->
                    _actividadesState.value = ListaActividadesState.Success(actividades)
                }
        }
    }
    
    // Guardar una actividad
    fun guardarActividad(actividad: RegistroActividad) {
        viewModelScope.launch {
            val resultado = cultivoRepository.saveActividad(actividad)
            
            if (resultado.isSuccess) {
                _mensajeOperacion.value = "Actividad guardada correctamente"
            } else {
                _mensajeOperacion.value = "Error al guardar la actividad: ${resultado.exceptionOrNull()?.message}"
            }
        }
    }
    
    // Eliminar una actividad
    fun eliminarActividad(actividadId: String) {
        viewModelScope.launch {
            val resultado = cultivoRepository.deleteActividad(actividadId)
            
            if (resultado.isSuccess) {
                _mensajeOperacion.value = "Actividad eliminada correctamente"
            } else {
                _mensajeOperacion.value = "Error al eliminar la actividad: ${resultado.exceptionOrNull()?.message}"
            }
        }
    }
    
    // Cargar cosechas de un cultivo específico
    fun cargarCosechas(cultivoId: String) {
        viewModelScope.launch {
            _cosechasState.value = ListaCosechasState.Loading
            
            cultivoRepository.getCosechas(cultivoId)
                .catch { e ->
                    _cosechasState.value = ListaCosechasState.Error("Error al cargar cosechas: ${e.message}")
                }
                .collect { cosechas ->
                    _cosechasState.value = ListaCosechasState.Success(cosechas)
                }
        }
    }
    
    // Guardar una cosecha
    fun guardarCosecha(cosecha: RegistroCosecha) {
        viewModelScope.launch {
            val resultado = cultivoRepository.saveCosecha(cosecha)
            
            if (resultado.isSuccess) {
                _mensajeOperacion.value = "Cosecha guardada correctamente"
            } else {
                _mensajeOperacion.value = "Error al guardar la cosecha: ${resultado.exceptionOrNull()?.message}"
            }
        }
    }
    
    // Eliminar una cosecha
    fun eliminarCosecha(cosechaId: String) {
        viewModelScope.launch {
            val resultado = cultivoRepository.deleteCosecha(cosechaId)
            
            if (resultado.isSuccess) {
                _mensajeOperacion.value = "Cosecha eliminada correctamente"
            } else {
                _mensajeOperacion.value = "Error al eliminar la cosecha: ${resultado.exceptionOrNull()?.message}"
            }
        }
    }
    
    // Limpiar el mensaje de operación
    fun clearMensajeOperacion() {
        _mensajeOperacion.value = null
    }
} 