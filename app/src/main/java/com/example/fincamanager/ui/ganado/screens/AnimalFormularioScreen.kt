package com.example.fincamanager.ui.ganado.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults as MaterialTextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fincamanager.data.model.ganado.Animal
import com.example.fincamanager.data.model.ganado.EstadoAnimal
import com.example.fincamanager.ui.ganado.GanadoViewModel
import com.example.fincamanager.ui.components.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de formulario para crear o editar un animal.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalFormularioScreen(
    navController: NavController,
    animalId: String? = null,
    viewModel: GanadoViewModel = hiltViewModel()
) {
    // Estados para los diferentes diálogos
    var mostrarDialogoNacimiento by remember { mutableStateOf(false) }
    var mostrarDialogoAdquisicion by remember { mutableStateOf(false) }
    var mostrarDialogoEstado by remember { mutableStateOf(false) }
    var mostrarDialogoEspecie by remember { mutableStateOf(false) }
    
    // Estado del formulario
    val formularioAnimal by viewModel.formularioAnimalState.collectAsState()
    
    // Especies disponibles
    val especies by viewModel.especiesDisponibles.collectAsState()
    
    // Mensaje de operación
    val mensajeOperacion by viewModel.mensajeOperacion.collectAsState()
    
    // Snackbar para mensajes
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Formateo de fechas para mostrar
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Cargar el animal si se está editando
    LaunchedEffect(animalId) {
        if (!animalId.isNullOrEmpty()) {
            viewModel.getAnimalById(animalId)
        } else {
            viewModel.limpiarFormularioAnimal()
        }
    }
    
    // Mostrar mensajes
    LaunchedEffect(mensajeOperacion) {
        mensajeOperacion?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMensajeOperacion()
        }
    }
    
    // Si el animal está cargado y es edición, actualizar el formulario
    LaunchedEffect(viewModel.animalSeleccionado.collectAsState().value, animalId) {
        if (!animalId.isNullOrEmpty()) {
            viewModel.animalSeleccionado.value?.let { animal ->
                viewModel.actualizarFormularioAnimal(animal)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (animalId.isNullOrEmpty()) "Nuevo Animal" else "Editar Animal") 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.guardarAnimal() }) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Guardar"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Información Básica",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo de identificación
            OutlinedTextField(
                value = formularioAnimal.identificacion,
                onValueChange = { viewModel.actualizarCampoFormularioAnimal("identificacion", it) },
                label = { Text("Identificación *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Campo de nombre
            OutlinedTextField(
                value = formularioAnimal.nombre,
                onValueChange = { viewModel.actualizarCampoFormularioAnimal("nombre", it) },
                label = { Text("Nombre (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo de especie con selector
            OutlinedTextField(
                value = formularioAnimal.especie,
                onValueChange = { },
                label = { Text("Especie *") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { mostrarDialogoEspecie = true }) {
                        Icon(Icons.Default.CalendarMonth, "Seleccionar especie")
                    }
                },
                colors = MaterialTextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                singleLine = true,
                enabled = false
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Campo de raza
            OutlinedTextField(
                value = formularioAnimal.raza,
                onValueChange = { viewModel.actualizarCampoFormularioAnimal("raza", it) },
                label = { Text("Raza") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Campo de género
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Género:", modifier = Modifier.width(80.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = formularioAnimal.genero == "Macho",
                        onClick = { viewModel.actualizarCampoFormularioAnimal("genero", "Macho") }
                    )
                    Text("Macho")
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    RadioButton(
                        selected = formularioAnimal.genero == "Hembra",
                        onClick = { viewModel.actualizarCampoFormularioAnimal("genero", "Hembra") }
                    )
                    Text("Hembra")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Campo de fecha de nacimiento
            OutlinedTextField(
                value = formularioAnimal.fechaNacimiento?.let { formatoFecha.format(it) } ?: "",
                onValueChange = { },
                label = { Text("Fecha de Nacimiento") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { mostrarDialogoNacimiento = true }) {
                        Icon(Icons.Default.CalendarMonth, "Seleccionar fecha")
                    }
                },
                colors = MaterialTextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                singleLine = true,
                enabled = false
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Campo de peso
            OutlinedTextField(
                value = if (formularioAnimal.peso > 0) formularioAnimal.peso.toString() else "",
                onValueChange = { 
                    try {
                        val peso = if (it.isEmpty()) 0.0 else it.toDouble()
                        viewModel.actualizarCampoFormularioAnimal("peso", peso)
                    } catch (e: NumberFormatException) {
                        // Ignorar entrada no válida
                    }
                },
                label = { Text("Peso (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Campo de color
            OutlinedTextField(
                value = formularioAnimal.color,
                onValueChange = { viewModel.actualizarCampoFormularioAnimal("color", it) },
                label = { Text("Color") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Información de Origen",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo de procedencia
            OutlinedTextField(
                value = formularioAnimal.procedencia,
                onValueChange = { viewModel.actualizarCampoFormularioAnimal("procedencia", it) },
                label = { Text("Procedencia") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Campo de ID de la madre
            OutlinedTextField(
                value = formularioAnimal.idMadre,
                onValueChange = { viewModel.actualizarCampoFormularioAnimal("idMadre", it) },
                label = { Text("ID de la Madre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Campo de ID del padre
            OutlinedTextField(
                value = formularioAnimal.idPadre,
                onValueChange = { viewModel.actualizarCampoFormularioAnimal("idPadre", it) },
                label = { Text("ID del Padre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Campo de fecha de adquisición
            OutlinedTextField(
                value = formularioAnimal.fechaAdquisicion?.let { formatoFecha.format(it) } ?: "",
                onValueChange = { },
                label = { Text("Fecha de Adquisición") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { mostrarDialogoAdquisicion = true }) {
                        Icon(Icons.Default.CalendarMonth, "Seleccionar fecha")
                    }
                },
                colors = MaterialTextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                singleLine = true,
                enabled = false
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Campo de precio de adquisición
            OutlinedTextField(
                value = if (formularioAnimal.precioAdquisicion > 0) formularioAnimal.precioAdquisicion.toString() else "",
                onValueChange = { 
                    try {
                        val precio = if (it.isEmpty()) 0.0 else it.toDouble()
                        viewModel.actualizarCampoFormularioAnimal("precioAdquisicion", precio)
                    } catch (e: NumberFormatException) {
                        // Ignorar entrada no válida
                    }
                },
                label = { Text("Precio de Adquisición") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Si es edición, mostrar campo de estado
            if (!animalId.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Estado",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Campo de estado
                OutlinedTextField(
                    value = formularioAnimal.estado.name,
                    onValueChange = { },
                    label = { Text("Estado del Animal") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { mostrarDialogoEstado = true }) {
                            Icon(Icons.Default.CalendarMonth, "Seleccionar estado")
                        }
                    },
                    colors = MaterialTextFieldDefaults.outlinedTextFieldColors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    singleLine = true,
                    enabled = false
                )
            }
            
            Spacer(modifier = Modifier.height(80.dp)) // Espacio para el FAB
        }
    }
    
    // Diálogo de selección de fecha de nacimiento
    if (mostrarDialogoNacimiento) {
        DatePickerDialog(
            onDateSelected = { fecha ->
                viewModel.actualizarCampoFormularioAnimal("fechaNacimiento", fecha)
            },
            onDismiss = { mostrarDialogoNacimiento = false },
            initialDate = formularioAnimal.fechaNacimiento ?: Date()
        )
    }
    
    // Diálogo de selección de fecha de adquisición
    if (mostrarDialogoAdquisicion) {
        DatePickerDialog(
            onDateSelected = { fecha ->
                viewModel.actualizarCampoFormularioAnimal("fechaAdquisicion", fecha)
            },
            onDismiss = { mostrarDialogoAdquisicion = false },
            initialDate = formularioAnimal.fechaAdquisicion ?: Date()
        )
    }
    
    // Diálogo de selección de estado
    if (mostrarDialogoEstado) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEstado = false },
            title = { Text("Seleccionar Estado") },
            text = { 
                Column {
                    EstadoAnimal.values().forEach { estado ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.actualizarCampoFormularioAnimal("estado", estado)
                                    mostrarDialogoEstado = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = formularioAnimal.estado == estado,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(estado.name)
                        }
                    }
                }
            },
            confirmButton = { },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEstado = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de selección de especie
    if (mostrarDialogoEspecie) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEspecie = false },
            title = { Text("Seleccionar Especie") },
            text = { 
                Column {
                    especies.forEach { especie ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.actualizarCampoFormularioAnimal("especie", especie)
                                    mostrarDialogoEspecie = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = formularioAnimal.especie == especie,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(especie)
                        }
                    }
                }
            },
            confirmButton = { },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEspecie = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

 