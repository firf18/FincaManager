package com.example.fincamanager.ui.ganado.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fincamanager.data.model.ganado.RegistroSanitario
import com.example.fincamanager.ui.ganado.GanadoViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Pantalla que muestra los registros sanitarios de un animal específico.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrosSanitariosScreen(
    navController: NavController,
    animalId: String,
    viewModel: GanadoViewModel = hiltViewModel(),
    onNavigateToFormularioRegistroSanitario: (String, String?) -> Unit
) {
    // Colectar el estado de los registros sanitarios
    val registrosSanitariosState by viewModel.registrosSanitariosState.collectAsState()
    
    // Colectar el animal seleccionado
    val animalSeleccionado by viewModel.animalSeleccionado.collectAsState()
    
    // Colectar el mensaje de operación
    val mensajeOperacion by viewModel.mensajeOperacion.collectAsState()
    
    // Mostrar snackbar con mensajes
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Variable para controlar el diálogo de confirmación de eliminación
    var registroAEliminar by remember { mutableStateOf<String?>(null) }
    
    // Cargar el animal y sus registros sanitarios
    LaunchedEffect(animalId) {
        viewModel.cargarAnimal(animalId)
        viewModel.cargarRegistrosSanitarios(animalId)
    }
    
    // Mostrar mensaje de operación
    LaunchedEffect(mensajeOperacion) {
        mensajeOperacion?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMensajeOperacion()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Registros Sanitarios" + 
                            (animalSeleccionado?.let { " - ${it.identificacion}" } ?: "")
                    )
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
            FloatingActionButton(
                onClick = { onNavigateToFormularioRegistroSanitario(animalId, null) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar registro sanitario"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (registrosSanitariosState) {
                is GanadoViewModel.ListaRegistrosSanitariosState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is GanadoViewModel.ListaRegistrosSanitariosState.Success -> {
                    val registros = (registrosSanitariosState as GanadoViewModel.ListaRegistrosSanitariosState.Success).registros
                    if (registros.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay registros sanitarios para este animal",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            items(registros) { registro ->
                                RegistroSanitarioItem(
                                    registro = registro,
                                    onClick = { onNavigateToFormularioRegistroSanitario(animalId, registro.id) },
                                    onEliminar = { registroAEliminar = registro.id }
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    thickness = 1.dp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
                is GanadoViewModel.ListaRegistrosSanitariosState.Error -> {
                    val errorMessage = (registrosSanitariosState as GanadoViewModel.ListaRegistrosSanitariosState.Error).mensaje
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.cargarRegistrosSanitarios(animalId) }
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Diálogo de confirmación para eliminar
    registroAEliminar?.let { registroId ->
        AlertDialog(
            onDismissRequest = { registroAEliminar = null },
            title = { Text("Eliminar Registro Sanitario") },
            text = { Text("¿Estás seguro de que deseas eliminar este registro? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarRegistroSanitario(registroId)
                        registroAEliminar = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { registroAEliminar = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Componente para mostrar un registro sanitario individual en la lista.
 */
@Composable
fun RegistroSanitarioItem(
    registro: RegistroSanitario,
    onClick: () -> Unit,
    onEliminar: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Encabezado: Fecha y Tipo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormat.format(registro.fecha),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = registro.tipo.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Descripción
            if (registro.descripcion.isNotBlank()) {
                Text(
                    text = registro.descripcion,
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Detalles del tratamiento
            if (registro.producto.isNotBlank()) {
                Text(
                    text = "Producto: ${registro.producto}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (registro.dosis.isNotBlank()) {
                Text(
                    text = "Dosis: ${registro.dosis}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (registro.viaMedicacion.isNotBlank()) {
                Text(
                    text = "Vía: ${registro.viaMedicacion}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (registro.responsable.isNotBlank()) {
                Text(
                    text = "Responsable: ${registro.responsable}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Próximo tratamiento
            registro.fechaProximoTratamiento?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Próximo tratamiento: ${dateFormat.format(it)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Botón para eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onEliminar
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
} 