package com.example.fincamanager.ui.ganado

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fincamanager.data.model.ganado.*
import com.example.fincamanager.data.repository.GanadoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel para la gestión ganadera.
 * 
 * Este ViewModel gestiona los datos y la lógica de negocio relacionada con
 * el módulo de ganado, sirviendo como intermediario entre la UI y el repositorio.
 */
@HiltViewModel
class GanadoViewModel @Inject constructor(
    private val repository: GanadoRepository
) : ViewModel() {

    // Estado para la lista de animales
    private val _animalesState = MutableStateFlow<ListaAnimalesState>(ListaAnimalesState.Loading)
    val animalesState: StateFlow<ListaAnimalesState> = _animalesState.asStateFlow()

    // Animal seleccionado actualmente
    private val _animalSeleccionado = MutableStateFlow<Animal?>(null)
    val animalSeleccionado: StateFlow<Animal?> = _animalSeleccionado.asStateFlow()

    // Estado para la lista de registros sanitarios del animal seleccionado
    private val _registrosSanitariosState = MutableStateFlow<ListaRegistrosSanitariosState>(ListaRegistrosSanitariosState.Loading)
    val registrosSanitariosState: StateFlow<ListaRegistrosSanitariosState> = _registrosSanitariosState.asStateFlow()

    // Estado para la lista de registros de producción de leche del animal seleccionado
    private val _produccionLecheState = MutableStateFlow<ListaProduccionLecheState>(ListaProduccionLecheState.Loading)
    val produccionLecheState: StateFlow<ListaProduccionLecheState> = _produccionLecheState.asStateFlow()

    // Estado para la lista de registros reproductivos del animal seleccionado
    private val _registrosReproduccionState = MutableStateFlow<ListaRegistrosReproduccionState>(ListaRegistrosReproduccionState.Loading)
    val registrosReproduccionState: StateFlow<ListaRegistrosReproduccionState> = _registrosReproduccionState.asStateFlow()

    // Estado actual del formulario de animal
    private val _formularioAnimalState = MutableStateFlow(Animal())
    val formularioAnimalState: StateFlow<Animal> = _formularioAnimalState.asStateFlow()

    // Estado actual del registro sanitario en edición
    private val _formularioRegistroSanitarioState = MutableStateFlow(RegistroSanitario(animalId = ""))
    val formularioRegistroSanitarioState: StateFlow<RegistroSanitario> = _formularioRegistroSanitarioState.asStateFlow()

    // Estado actual del registro de producción de leche en edición
    private val _formularioProduccionLecheState = MutableStateFlow(ProduccionLeche(animalId = ""))
    val formularioProduccionLecheState: StateFlow<ProduccionLeche> = _formularioProduccionLecheState.asStateFlow()

    // Estado actual del registro reproductivo en edición
    private val _formularioRegistroReproduccionState = MutableStateFlow(RegistroReproduccion(animalId = ""))
    val formularioRegistroReproduccionState: StateFlow<RegistroReproduccion> = _formularioRegistroReproduccionState.asStateFlow()

    // Especies disponibles
    private val _especiesDisponibles = MutableStateFlow(
        listOf(
            "Vaca", "Caballo", "Toro", "Búfalo", "Cerdo", 
            "Gallina ponedora", "Pollo de engorde", "Pato", "Ganso", 
            "Avestruz", "Paloma", "Codorniz", "Abeja", "Pez"
        )
    )
    val especiesDisponibles: StateFlow<List<String>> = _especiesDisponibles.asStateFlow()

    // Especies seleccionadas por el usuario
    private val _especiesSeleccionadas = MutableStateFlow<Set<String>>(setOf())
    val especiesSeleccionadas: StateFlow<Set<String>> = _especiesSeleccionadas.asStateFlow()

    // Variable para almacenar la especie seleccionada para filtrar
    private val _filtroEspecie = MutableStateFlow<String?>(null)
    val filtroEspecie: StateFlow<String?> = _filtroEspecie.asStateFlow()

    // Mensaje de operación
    private val _mensajeOperacion = MutableStateFlow<String?>(null)
    val mensajeOperacion: StateFlow<String?> = _mensajeOperacion.asStateFlow()

    // Inicialización
    init {
        cargarAnimales()
        // Cargar especies seleccionadas desde DataStore
        cargarEspeciesSeleccionadas()
    }

    // ---- Funciones para Animal ----

    // Cargar todos los animales
    fun cargarAnimales() {
        viewModelScope.launch {
            _animalesState.value = ListaAnimalesState.Loading
            try {
                repository.getAllAnimales()
                    .catch { e ->
                        _animalesState.value = ListaAnimalesState.Error(e.message ?: "Error desconocido")
                        Log.e("GanadoViewModel", "Error cargando animales", e)
                    }
                    .collect { animales ->
                        _animalesState.value = ListaAnimalesState.Success(animales)
                    }
            } catch (e: Exception) {
                _animalesState.value = ListaAnimalesState.Error(e.message ?: "Error desconocido")
                Log.e("GanadoViewModel", "Error cargando animales", e)
            }
        }
    }

    // Seleccionar un animal para ver sus detalles
    fun seleccionarAnimal(animal: Animal) {
        _animalSeleccionado.value = animal
        // Cargar datos relacionados con el animal
        cargarRegistrosSanitarios(animal.id)
        cargarProduccionLeche(animal.id)
        cargarRegistrosReproduccion(animal.id)
    }

    // Deseleccionar animal
    fun deseleccionarAnimal() {
        _animalSeleccionado.value = null
        // Limpiar datos relacionados
        _registrosSanitariosState.value = ListaRegistrosSanitariosState.Loading
        _produccionLecheState.value = ListaProduccionLecheState.Loading
        _registrosReproduccionState.value = ListaRegistrosReproduccionState.Loading
    }

    // Actualizar el estado del formulario de animal
    fun actualizarFormularioAnimal(animal: Animal) {
        _formularioAnimalState.value = animal
    }

    // Actualizar un campo específico del formulario de animal
    fun actualizarCampoFormularioAnimal(clave: String, valor: Any?) {
        val animal = _formularioAnimalState.value
        _formularioAnimalState.value = when (clave) {
            "identificacion" -> animal.copy(identificacion = valor as String)
            "nombre" -> animal.copy(nombre = valor as String)
            "especie" -> animal.copy(especie = valor as String)
            "raza" -> animal.copy(raza = valor as String)
            "genero" -> animal.copy(genero = valor as String)
            "fechaNacimiento" -> animal.copy(fechaNacimiento = valor as Date?)
            "peso" -> animal.copy(peso = (valor as? Double) ?: 0.0)
            "color" -> animal.copy(color = valor as String)
            "procedencia" -> animal.copy(procedencia = valor as String)
            "idMadre" -> animal.copy(idMadre = valor as String)
            "idPadre" -> animal.copy(idPadre = valor as String)
            "fechaAdquisicion" -> animal.copy(fechaAdquisicion = valor as Date?)
            "precioAdquisicion" -> animal.copy(precioAdquisicion = (valor as? Double) ?: 0.0)
            "estado" -> animal.copy(estado = valor as EstadoAnimal)
            else -> animal
        }
    }

    // Guardar animal (nuevo o actualización)
    fun guardarAnimal() {
        viewModelScope.launch {
            try {
                val animal = _formularioAnimalState.value
                
                if (animal.id.isEmpty()) {
                    // Es un nuevo animal
                    val nuevoAnimal = animal.copy(
                        fechaCreacion = Date(),
                        fechaActualizacion = Date()
                    )
                    val id = repository.saveAnimal(nuevoAnimal)
                    _mensajeOperacion.value = "Animal guardado con éxito"
                    // Refrescar la lista
                    cargarAnimales()
                } else {
                    // Es una actualización
                    val exito = repository.updateAnimal(animal)
                    if (exito) {
                        _mensajeOperacion.value = "Animal actualizado con éxito"
                        // Refrescar la lista y el animal seleccionado
                        cargarAnimales()
                        _animalSeleccionado.value = animal
                    } else {
                        _mensajeOperacion.value = "Error al actualizar el animal"
                    }
                }
                // Limpiar el formulario después de guardar
                limpiarFormularioAnimal()
            } catch (e: Exception) {
                _mensajeOperacion.value = "Error: ${e.message}"
                Log.e("GanadoViewModel", "Error al guardar animal", e)
            }
        }
    }

    // Eliminar animal
    fun eliminarAnimal(animalId: String) {
        viewModelScope.launch {
            try {
                val exito = repository.deleteAnimal(animalId)
                if (exito) {
                    _mensajeOperacion.value = "Animal eliminado con éxito"
                    // Si el animal eliminado era el seleccionado, deseleccionarlo
                    if (_animalSeleccionado.value?.id == animalId) {
                        deseleccionarAnimal()
                    }
                    // Refrescar la lista
                    cargarAnimales()
                } else {
                    _mensajeOperacion.value = "Error al eliminar el animal"
                }
            } catch (e: Exception) {
                _mensajeOperacion.value = "Error: ${e.message}"
                Log.e("GanadoViewModel", "Error al eliminar animal", e)
            }
        }
    }

    // Limpiar el formulario de animal
    fun limpiarFormularioAnimal() {
        _formularioAnimalState.value = Animal()
    }

    // ---- Funciones para RegistroSanitario ----

    // Cargar registros sanitarios de un animal
    fun cargarRegistrosSanitarios(animalId: String) {
        viewModelScope.launch {
            _registrosSanitariosState.value = ListaRegistrosSanitariosState.Loading
            try {
                repository.getRegistrosSanitariosByAnimalId(animalId)
                    .catch { e ->
                        _registrosSanitariosState.value = ListaRegistrosSanitariosState.Error(e.message ?: "Error desconocido")
                        Log.e("GanadoViewModel", "Error cargando registros sanitarios", e)
                    }
                    .collect { registros ->
                        _registrosSanitariosState.value = ListaRegistrosSanitariosState.Success(registros)
                    }
            } catch (e: Exception) {
                _registrosSanitariosState.value = ListaRegistrosSanitariosState.Error(e.message ?: "Error desconocido")
                Log.e("GanadoViewModel", "Error cargando registros sanitarios", e)
            }
        }
    }

    // Inicializar un nuevo registro sanitario para un animal
    fun iniciarNuevoRegistroSanitario(animalId: String) {
        _formularioRegistroSanitarioState.value = RegistroSanitario(
            animalId = animalId,
            fecha = Date()
        )
    }

    // Actualizar un campo específico del formulario de registro sanitario
    fun actualizarCampoFormularioRegistroSanitario(clave: String, valor: Any?) {
        val registro = _formularioRegistroSanitarioState.value
        _formularioRegistroSanitarioState.value = when (clave) {
            "fecha" -> registro.copy(fecha = valor as Date)
            "tipo" -> registro.copy(tipo = valor as TipoRegistroSanitario)
            "descripcion" -> registro.copy(descripcion = valor as String)
            "producto" -> registro.copy(producto = valor as String)
            "dosis" -> registro.copy(dosis = valor as String)
            "viaMedicacion" -> registro.copy(viaMedicacion = valor as String)
            "responsable" -> registro.copy(responsable = valor as String)
            "observaciones" -> registro.copy(observaciones = valor as String)
            "fechaProximoTratamiento" -> registro.copy(fechaProximoTratamiento = valor as Date?)
            else -> registro
        }
    }

    // Guardar registro sanitario (nuevo o actualización)
    fun guardarRegistroSanitario() {
        viewModelScope.launch {
            try {
                val registro = _formularioRegistroSanitarioState.value
                
                if (registro.id.isEmpty()) {
                    // Es un nuevo registro
                    val nuevoRegistro = registro.copy(
                        fechaCreacion = Date(),
                        fechaActualizacion = Date()
                    )
                    val id = repository.saveRegistroSanitario(nuevoRegistro)
                    _mensajeOperacion.value = "Registro sanitario guardado con éxito"
                } else {
                    // Es una actualización
                    val exito = repository.updateRegistroSanitario(registro)
                    if (exito) {
                        _mensajeOperacion.value = "Registro sanitario actualizado con éxito"
                    } else {
                        _mensajeOperacion.value = "Error al actualizar el registro sanitario"
                    }
                }
                // Refrescar la lista de registros sanitarios
                _animalSeleccionado.value?.let { animal ->
                    cargarRegistrosSanitarios(animal.id)
                }
                // Limpiar el formulario después de guardar
                limpiarFormularioRegistroSanitario()
            } catch (e: Exception) {
                _mensajeOperacion.value = "Error: ${e.message}"
                Log.e("GanadoViewModel", "Error al guardar registro sanitario", e)
            }
        }
    }

    // Eliminar registro sanitario
    fun eliminarRegistroSanitario(registroId: String) {
        viewModelScope.launch {
            try {
                val exito = repository.deleteRegistroSanitario(registroId)
                if (exito) {
                    _mensajeOperacion.value = "Registro sanitario eliminado con éxito"
                    // Refrescar la lista de registros sanitarios
                    _animalSeleccionado.value?.let { animal ->
                        cargarRegistrosSanitarios(animal.id)
                    }
                } else {
                    _mensajeOperacion.value = "Error al eliminar el registro sanitario"
                }
            } catch (e: Exception) {
                _mensajeOperacion.value = "Error: ${e.message}"
                Log.e("GanadoViewModel", "Error al eliminar registro sanitario", e)
            }
        }
    }

    // Limpiar el formulario de registro sanitario
    fun limpiarFormularioRegistroSanitario() {
        val animalId = _animalSeleccionado.value?.id ?: ""
        _formularioRegistroSanitarioState.value = RegistroSanitario(animalId = animalId)
    }

    // ---- Funciones para ProduccionLeche ----

    // Cargar registros de producción de leche de un animal
    fun cargarProduccionLeche(animalId: String) {
        viewModelScope.launch {
            _produccionLecheState.value = ListaProduccionLecheState.Loading
            try {
                repository.getProduccionLecheByAnimalId(animalId)
                    .catch { e ->
                        _produccionLecheState.value = ListaProduccionLecheState.Error(e.message ?: "Error desconocido")
                        Log.e("GanadoViewModel", "Error cargando producción de leche", e)
                    }
                    .collect { registros ->
                        _produccionLecheState.value = ListaProduccionLecheState.Success(registros)
                    }
            } catch (e: Exception) {
                _produccionLecheState.value = ListaProduccionLecheState.Error(e.message ?: "Error desconocido")
                Log.e("GanadoViewModel", "Error cargando producción de leche", e)
            }
        }
    }

    // Inicializar un nuevo registro de producción de leche para un animal
    fun iniciarNuevaProduccionLeche(animalId: String) {
        _formularioProduccionLecheState.value = ProduccionLeche(
            animalId = animalId,
            fecha = Date()
        )
    }

    // Actualizar un campo específico del formulario de producción de leche
    fun actualizarCampoFormularioProduccionLeche(clave: String, valor: Any?) {
        val produccion = _formularioProduccionLecheState.value
        _formularioProduccionLecheState.value = when (clave) {
            "fecha" -> produccion.copy(fecha = valor as Date)
            "horario" -> produccion.copy(horario = valor as HorarioOrdenio)
            "cantidad" -> produccion.copy(cantidad = (valor as? Double) ?: 0.0)
            "calidad" -> produccion.copy(calidad = valor as String)
            "porcentajeGrasa" -> produccion.copy(porcentajeGrasa = valor as Double?)
            "porcentajeProteina" -> produccion.copy(porcentajeProteina = valor as Double?)
            "observaciones" -> produccion.copy(observaciones = valor as String)
            else -> produccion
        }
    }

    // Guardar registro de producción de leche (nuevo o actualización)
    fun guardarProduccionLeche() {
        viewModelScope.launch {
            try {
                val produccion = _formularioProduccionLecheState.value
                
                if (produccion.id.isEmpty()) {
                    // Es un nuevo registro
                    val nuevaProduccion = produccion.copy(
                        fechaCreacion = Date(),
                        fechaActualizacion = Date()
                    )
                    val id = repository.saveProduccionLeche(nuevaProduccion)
                    _mensajeOperacion.value = "Registro de producción de leche guardado con éxito"
                } else {
                    // Es una actualización
                    val exito = repository.updateProduccionLeche(produccion)
                    if (exito) {
                        _mensajeOperacion.value = "Registro de producción de leche actualizado con éxito"
                    } else {
                        _mensajeOperacion.value = "Error al actualizar el registro de producción de leche"
                    }
                }
                // Refrescar la lista de registros de producción de leche
                _animalSeleccionado.value?.let { animal ->
                    cargarProduccionLeche(animal.id)
                }
                // Limpiar el formulario después de guardar
                limpiarFormularioProduccionLeche()
            } catch (e: Exception) {
                _mensajeOperacion.value = "Error: ${e.message}"
                Log.e("GanadoViewModel", "Error al guardar producción de leche", e)
            }
        }
    }

    // Eliminar registro de producción de leche
    fun eliminarProduccionLeche(produccionId: String) {
        viewModelScope.launch {
            try {
                val exito = repository.deleteProduccionLeche(produccionId)
                if (exito) {
                    _mensajeOperacion.value = "Registro de producción de leche eliminado con éxito"
                    // Refrescar la lista de registros de producción de leche
                    _animalSeleccionado.value?.let { animal ->
                        cargarProduccionLeche(animal.id)
                    }
                } else {
                    _mensajeOperacion.value = "Error al eliminar el registro de producción de leche"
                }
            } catch (e: Exception) {
                _mensajeOperacion.value = "Error: ${e.message}"
                Log.e("GanadoViewModel", "Error al eliminar producción de leche", e)
            }
        }
    }

    // Limpiar el formulario de producción de leche
    fun limpiarFormularioProduccionLeche() {
        val animalId = _animalSeleccionado.value?.id ?: ""
        _formularioProduccionLecheState.value = ProduccionLeche(animalId = animalId)
    }

    // ---- Funciones para RegistroReproduccion ----

    // Cargar registros reproductivos de un animal
    fun cargarRegistrosReproduccion(animalId: String) {
        viewModelScope.launch {
            _registrosReproduccionState.value = ListaRegistrosReproduccionState.Loading
            try {
                repository.getRegistrosReproduccionByAnimalId(animalId)
                    .catch { e ->
                        _registrosReproduccionState.value = ListaRegistrosReproduccionState.Error(e.message ?: "Error desconocido")
                        Log.e("GanadoViewModel", "Error cargando registros reproductivos", e)
                    }
                    .collect { registros ->
                        _registrosReproduccionState.value = ListaRegistrosReproduccionState.Success(registros)
                    }
            } catch (e: Exception) {
                _registrosReproduccionState.value = ListaRegistrosReproduccionState.Error(e.message ?: "Error desconocido")
                Log.e("GanadoViewModel", "Error cargando registros reproductivos", e)
            }
        }
    }

    // Inicializar un nuevo registro reproductivo para un animal
    fun iniciarNuevoRegistroReproduccion(animalId: String) {
        _formularioRegistroReproduccionState.value = RegistroReproduccion(
            animalId = animalId,
            fecha = Date()
        )
    }

    // Actualizar un campo específico del formulario de registro reproductivo
    fun actualizarCampoFormularioRegistroReproduccion(clave: String, valor: Any?) {
        val registro = _formularioRegistroReproduccionState.value
        _formularioRegistroReproduccionState.value = when (clave) {
            "fecha" -> registro.copy(fecha = valor as Date)
            "tipoEvento" -> registro.copy(tipoEvento = valor as TipoEventoReproductivo)
            "idMacho" -> registro.copy(idMacho = valor as String)
            "tipoSemen" -> registro.copy(tipoSemen = valor as String)
            "inseminador" -> registro.copy(inseminador = valor as String)
            "cantidadCrias" -> registro.copy(cantidadCrias = (valor as? Int) ?: 0)
            "idsCrias" -> registro.copy(idsCrias = when (valor) {
                is List<*> -> valor.filterIsInstance<String>()
                null -> emptyList()
                else -> emptyList()
            })
            "complicaciones" -> registro.copy(complicaciones = valor as String)
            "resultado" -> registro.copy(resultado = valor as Boolean?)
            "metodoDiagnostico" -> registro.copy(metodoDiagnostico = valor as String)
            "diasGestacion" -> registro.copy(diasGestacion = valor as Int?)
            "fechaProbableParto" -> registro.copy(fechaProbableParto = valor as Date?)
            "observaciones" -> registro.copy(observaciones = valor as String)
            else -> registro
        }
    }

    // Guardar registro reproductivo (nuevo o actualización)
    fun guardarRegistroReproduccion() {
        viewModelScope.launch {
            try {
                val registro = _formularioRegistroReproduccionState.value
                
                if (registro.id.isEmpty()) {
                    // Es un nuevo registro
                    val nuevoRegistro = registro.copy(
                        fechaCreacion = Date(),
                        fechaActualizacion = Date()
                    )
                    val id = repository.saveRegistroReproduccion(nuevoRegistro)
                    _mensajeOperacion.value = "Registro reproductivo guardado con éxito"
                } else {
                    // Es una actualización
                    val exito = repository.updateRegistroReproduccion(registro)
                    if (exito) {
                        _mensajeOperacion.value = "Registro reproductivo actualizado con éxito"
                    } else {
                        _mensajeOperacion.value = "Error al actualizar el registro reproductivo"
                    }
                }
                // Refrescar la lista de registros reproductivos
                _animalSeleccionado.value?.let { animal ->
                    cargarRegistrosReproduccion(animal.id)
                }
                // Limpiar el formulario después de guardar
                limpiarFormularioRegistroReproduccion()
            } catch (e: Exception) {
                _mensajeOperacion.value = "Error: ${e.message}"
                Log.e("GanadoViewModel", "Error al guardar registro reproductivo", e)
            }
        }
    }

    // Eliminar registro reproductivo
    fun eliminarRegistroReproduccion(registroId: String) {
        viewModelScope.launch {
            try {
                val exito = repository.deleteRegistroReproduccion(registroId)
                if (exito) {
                    _mensajeOperacion.value = "Registro reproductivo eliminado con éxito"
                    // Refrescar la lista de registros reproductivos
                    _animalSeleccionado.value?.let { animal ->
                        cargarRegistrosReproduccion(animal.id)
                    }
                } else {
                    _mensajeOperacion.value = "Error al eliminar el registro reproductivo"
                }
            } catch (e: Exception) {
                _mensajeOperacion.value = "Error: ${e.message}"
                Log.e("GanadoViewModel", "Error al eliminar registro reproductivo", e)
            }
        }
    }

    // Limpiar el formulario de registro reproductivo
    fun limpiarFormularioRegistroReproduccion() {
        val animalId = _animalSeleccionado.value?.id ?: ""
        _formularioRegistroReproduccionState.value = RegistroReproduccion(animalId = animalId)
    }

    // ---- Estados UI ----

    // Estado para la lista de animales
    sealed class ListaAnimalesState {
        data object Loading : ListaAnimalesState()
        data class Success(val animales: List<Animal>) : ListaAnimalesState()
        data class Error(val mensaje: String) : ListaAnimalesState()
    }

    // Estado para la lista de registros sanitarios
    sealed class ListaRegistrosSanitariosState {
        data object Loading : ListaRegistrosSanitariosState()
        data class Success(val registros: List<RegistroSanitario>) : ListaRegistrosSanitariosState()
        data class Error(val mensaje: String) : ListaRegistrosSanitariosState()
    }

    // Estado para la lista de producción de leche
    sealed class ListaProduccionLecheState {
        data object Loading : ListaProduccionLecheState()
        data class Success(val producciones: List<ProduccionLeche>) : ListaProduccionLecheState()
        data class Error(val mensaje: String) : ListaProduccionLecheState()
    }

    // Estado para la lista de registros reproductivos
    sealed class ListaRegistrosReproduccionState {
        data object Loading : ListaRegistrosReproduccionState()
        data class Success(val registros: List<RegistroReproduccion>) : ListaRegistrosReproduccionState()
        data class Error(val mensaje: String) : ListaRegistrosReproduccionState()
    }

    // Función para limpiar el mensaje de operación
    fun clearMensajeOperacion() {
        _mensajeOperacion.value = null
    }

    // Obtener animal por ID
    fun getAnimalById(animalId: String) {
        viewModelScope.launch {
            try {
                repository.getAnimalById(animalId)
                    .catch { e ->
                        _mensajeOperacion.value = "Error: ${e.message}"
                        Log.e("GanadoViewModel", "Error obteniendo animal por ID", e)
                    }
                    .collect { animal ->
                        if (animal != null) {
                            seleccionarAnimal(animal)
                        } else {
                            _mensajeOperacion.value = "Animal no encontrado"
                        }
                    }
            } catch (e: Exception) {
                _mensajeOperacion.value = "Error: ${e.message}"
                Log.e("GanadoViewModel", "Error obteniendo animal por ID", e)
            }
        }
    }
    
    // Cargar animal por ID (alias para getAnimalById para compatibilidad con las pantallas)
    fun cargarAnimal(animalId: String) {
        getAnimalById(animalId)
    }

    // ---- Funciones para gestión de especies ----

    // Cargar especies seleccionadas desde preferencias
    private fun cargarEspeciesSeleccionadas() {
        viewModelScope.launch {
            try {
                repository.getEspeciesSeleccionadas().collect { especies ->
                    _especiesSeleccionadas.value = especies
                }
            } catch (e: Exception) {
                Log.e("GanadoViewModel", "Error cargando especies seleccionadas", e)
            }
        }
    }

    // Alternar selección de especie
    fun toggleEspecieSeleccion(especie: String) {
        val especiesActuales = _especiesSeleccionadas.value.toMutableSet()
        if (especiesActuales.contains(especie)) {
            especiesActuales.remove(especie)
        } else {
            especiesActuales.add(especie)
        }
        _especiesSeleccionadas.value = especiesActuales
        
        // Guardar en preferencias
        viewModelScope.launch {
            repository.saveEspeciesSeleccionadas(especiesActuales)
        }
    }

    // Limpiar selección de especies
    fun limpiarEspeciesSeleccionadas() {
        _especiesSeleccionadas.value = setOf()
        viewModelScope.launch {
            repository.saveEspeciesSeleccionadas(setOf())
        }
    }

    // Configurar el filtro de especie activo
    fun setFiltroEspecie(especie: String) {
        _filtroEspecie.value = especie
    }

    // Cargar animales filtrados por especies seleccionadas
    fun cargarAnimalesFiltrados() {
        viewModelScope.launch {
            _animalesState.value = ListaAnimalesState.Loading
            try {
                val especies = _especiesSeleccionadas.value
                
                if (especies.isEmpty()) {
                    // Si no hay especies seleccionadas, mostrar todos los animales
                    cargarAnimales()
                    return@launch
                }
                
                // Si hay un filtro activo por especie específica, aplicarlo
                val filtroEspecie = _filtroEspecie.value
                
                repository.getAnimalesFiltrados(
                    especies = if (filtroEspecie != null) setOf(filtroEspecie) else especies
                ).catch { e ->
                    _animalesState.value = ListaAnimalesState.Error(e.message ?: "Error desconocido")
                    Log.e("GanadoViewModel", "Error cargando animales filtrados", e)
                }.collect { animales ->
                    _animalesState.value = ListaAnimalesState.Success(animales)
                }
            } catch (e: Exception) {
                _animalesState.value = ListaAnimalesState.Error(e.message ?: "Error desconocido")
                Log.e("GanadoViewModel", "Error cargando animales filtrados", e)
            }
        }
    }
} 