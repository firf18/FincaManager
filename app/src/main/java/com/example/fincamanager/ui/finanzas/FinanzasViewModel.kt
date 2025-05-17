package com.example.fincamanager.ui.finanzas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fincamanager.data.model.finanzas.Presupuesto
import com.example.fincamanager.data.model.finanzas.TipoTransaccion
import com.example.fincamanager.data.model.finanzas.Transaccion
import com.example.fincamanager.data.repository.FinanzasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FinanzasViewModel @Inject constructor(
    private val finanzasRepository: FinanzasRepository
) : ViewModel() {

    // Estado para la lista de transacciones
    private val _transaccionesState = MutableStateFlow<ListaTransaccionesState>(ListaTransaccionesState.Loading)
    val transaccionesState: StateFlow<ListaTransaccionesState> = _transaccionesState
    
    // Estado para la lista de presupuestos
    private val _presupuestosState = MutableStateFlow<ListaPresupuestosState>(ListaPresupuestosState.Loading)
    val presupuestosState: StateFlow<ListaPresupuestosState> = _presupuestosState
    
    // Estado para los totales de ingresos y egresos
    private val _balanceState = MutableStateFlow<BalanceState>(BalanceState.Loading)
    val balanceState: StateFlow<BalanceState> = _balanceState
    
    // Mensaje de operación para retroalimentación al usuario
    private val _mensajeOperacion = MutableStateFlow<String?>(null)
    val mensajeOperacion: StateFlow<String?> = _mensajeOperacion
    
    // Estados sellados para manejar los diferentes estados de la UI
    sealed class ListaTransaccionesState {
        object Loading : ListaTransaccionesState()
        data class Success(val transacciones: List<Transaccion>) : ListaTransaccionesState()
        data class Error(val mensaje: String) : ListaTransaccionesState()
    }
    
    sealed class ListaPresupuestosState {
        object Loading : ListaPresupuestosState()
        data class Success(val presupuestos: List<Presupuesto>) : ListaPresupuestosState()
        data class Error(val mensaje: String) : ListaPresupuestosState()
    }
    
    sealed class BalanceState {
        object Loading : BalanceState()
        data class Success(
            val totalIngresos: Double,
            val totalGastos: Double,
            val balance: Double
        ) : BalanceState()
        data class Error(val mensaje: String) : BalanceState()
    }
    
    // Cargar transacciones
    fun cargarTransacciones(fincaId: String) {
        viewModelScope.launch {
            _transaccionesState.value = ListaTransaccionesState.Loading
            
            finanzasRepository.getTransacciones(fincaId)
                .catch { e ->
                    _transaccionesState.value = ListaTransaccionesState.Error("Error al cargar transacciones: ${e.message}")
                }
                .collect { transacciones ->
                    _transaccionesState.value = ListaTransaccionesState.Success(transacciones)
                    calcularBalance(transacciones)
                }
        }
    }
    
    // Cargar transacciones por fecha
    fun cargarTransaccionesPorFecha(fincaId: String, fechaInicio: Date, fechaFin: Date) {
        viewModelScope.launch {
            _transaccionesState.value = ListaTransaccionesState.Loading
            
            finanzasRepository.getTransaccionesPorFecha(fincaId, fechaInicio, fechaFin)
                .catch { e ->
                    _transaccionesState.value = ListaTransaccionesState.Error("Error al cargar transacciones: ${e.message}")
                }
                .collect { transacciones ->
                    _transaccionesState.value = ListaTransaccionesState.Success(transacciones)
                    calcularBalance(transacciones)
                }
        }
    }
    
    // Cargar transacciones por área
    fun cargarTransaccionesPorArea(fincaId: String, area: String) {
        viewModelScope.launch {
            _transaccionesState.value = ListaTransaccionesState.Loading
            
            finanzasRepository.getTransaccionesPorArea(fincaId, area)
                .catch { e ->
                    _transaccionesState.value = ListaTransaccionesState.Error("Error al cargar transacciones: ${e.message}")
                }
                .collect { transacciones ->
                    _transaccionesState.value = ListaTransaccionesState.Success(transacciones)
                    calcularBalance(transacciones)
                }
        }
    }
    
    // Calcular balance a partir de transacciones
    private fun calcularBalance(transacciones: List<Transaccion>) {
        var totalIngresos = 0.0
        var totalGastos = 0.0
        
        transacciones.forEach { transaccion ->
            when (transaccion.tipo) {
                TipoTransaccion.INGRESO -> totalIngresos += transaccion.monto
                TipoTransaccion.GASTO -> totalGastos += transaccion.monto
                TipoTransaccion.TRANSFERENCIA -> {} // No afecta el balance
                TipoTransaccion.INVERSION -> totalGastos += transaccion.monto
                TipoTransaccion.PRESTAMO -> totalIngresos += transaccion.monto
                TipoTransaccion.PAGO_PRESTAMO -> totalGastos += transaccion.monto
            }
        }
        
        val balance = totalIngresos - totalGastos
        _balanceState.value = BalanceState.Success(totalIngresos, totalGastos, balance)
    }
    
    // Guardar o actualizar una transacción
    fun guardarTransaccion(transaccion: Transaccion) {
        viewModelScope.launch {
            val resultado = finanzasRepository.saveTransaccion(transaccion)
            
            if (resultado.isSuccess) {
                _mensajeOperacion.value = "Transacción guardada correctamente"
                // Recargar las transacciones para actualizar el balance
                cargarTransacciones(transaccion.fincaId)
            } else {
                _mensajeOperacion.value = "Error al guardar la transacción: ${resultado.exceptionOrNull()?.message}"
            }
        }
    }
    
    // Eliminar una transacción
    fun eliminarTransaccion(transaccionId: String, fincaId: String) {
        viewModelScope.launch {
            val resultado = finanzasRepository.deleteTransaccion(transaccionId)
            
            if (resultado.isSuccess) {
                _mensajeOperacion.value = "Transacción eliminada correctamente"
                // Recargar las transacciones para actualizar el balance
                cargarTransacciones(fincaId)
            } else {
                _mensajeOperacion.value = "Error al eliminar la transacción: ${resultado.exceptionOrNull()?.message}"
            }
        }
    }
    
    // Cargar presupuestos
    fun cargarPresupuestos(fincaId: String) {
        viewModelScope.launch {
            _presupuestosState.value = ListaPresupuestosState.Loading
            
            finanzasRepository.getPresupuestos(fincaId)
                .catch { e ->
                    _presupuestosState.value = ListaPresupuestosState.Error("Error al cargar presupuestos: ${e.message}")
                }
                .collect { presupuestos ->
                    _presupuestosState.value = ListaPresupuestosState.Success(presupuestos)
                }
        }
    }
    
    // Cargar presupuestos por área
    fun cargarPresupuestosPorArea(fincaId: String, area: String) {
        viewModelScope.launch {
            _presupuestosState.value = ListaPresupuestosState.Loading
            
            finanzasRepository.getPresupuestosPorArea(fincaId, area)
                .catch { e ->
                    _presupuestosState.value = ListaPresupuestosState.Error("Error al cargar presupuestos: ${e.message}")
                }
                .collect { presupuestos ->
                    _presupuestosState.value = ListaPresupuestosState.Success(presupuestos)
                }
        }
    }
    
    // Guardar o actualizar un presupuesto
    fun guardarPresupuesto(presupuesto: Presupuesto) {
        viewModelScope.launch {
            val resultado = finanzasRepository.savePresupuesto(presupuesto)
            
            if (resultado.isSuccess) {
                _mensajeOperacion.value = "Presupuesto guardado correctamente"
                cargarPresupuestos(presupuesto.fincaId)
            } else {
                _mensajeOperacion.value = "Error al guardar el presupuesto: ${resultado.exceptionOrNull()?.message}"
            }
        }
    }
    
    // Eliminar un presupuesto
    fun eliminarPresupuesto(presupuestoId: String, fincaId: String) {
        viewModelScope.launch {
            val resultado = finanzasRepository.deletePresupuesto(presupuestoId)
            
            if (resultado.isSuccess) {
                _mensajeOperacion.value = "Presupuesto eliminado correctamente"
                cargarPresupuestos(fincaId)
            } else {
                _mensajeOperacion.value = "Error al eliminar el presupuesto: ${resultado.exceptionOrNull()?.message}"
            }
        }
    }
    
    // Limpiar el mensaje de operación
    fun clearMensajeOperacion() {
        _mensajeOperacion.value = null
    }
} 