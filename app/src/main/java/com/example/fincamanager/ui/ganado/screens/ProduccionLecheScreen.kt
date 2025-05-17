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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fincamanager.data.model.ganado.ProduccionLeche
import com.example.fincamanager.ui.ganado.GanadoViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.HorizontalDivider

/**
 * Pantalla que muestra los registros de producción de leche de un animal específico.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduccionLecheScreen(
    navController: NavController,
    animalId: String,
    viewModel: GanadoViewModel = hiltViewModel(),
    onNavigateToFormularioProduccionLeche: (String, String?) -> Unit
) {
    // Colectar el estado de los registros de producción de leche
    val produccionLecheState by viewModel.produccionLecheState.collectAsState()
    
    // Colectar el animal seleccionado
    val animalSeleccionado by viewModel.animalSeleccionado.collectAsState()
    
    // Colectar el mensaje de operación
    val mensajeOperacion by viewModel.mensajeOperacion.collectAsState()
    
    // Mostrar snackbar con mensajes
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Variable para controlar el diálogo de confirmación de eliminación
    var registroAEliminar by remember { mutableStateOf<String?>(null) }
    
    // Calcular el total de producción de leche
    val totalProduccion = when (produccionLecheState) {
        is GanadoViewModel.ListaProduccionLecheState.Success -> {
            val producciones = (produccionLecheState as GanadoViewModel.ListaProduccionLecheState.Success).producciones
            producciones.sumOf { it.cantidad }
        }
        else -> 0.0
    }
    
    // Cargar el animal y sus registros de producción de leche
    LaunchedEffect(animalId) {
        viewModel.cargarAnimal(animalId)
        viewModel.cargarProduccionLeche(animalId)
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
                        text = "Producción de Leche" + 
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
                onClick = { onNavigateToFormularioProduccionLeche(animalId, null) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar registro de producción"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Mostrar el total de producción
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Producción Total",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$totalProduccion litros",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Contenido principal según el estado
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when (produccionLecheState) {
                    is GanadoViewModel.ListaProduccionLecheState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is GanadoViewModel.ListaProduccionLecheState.Success -> {
                        val producciones = (produccionLecheState as GanadoViewModel.ListaProduccionLecheState.Success).producciones
                        if (producciones.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No hay registros de producción de leche para este animal",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(producciones) { produccion ->
                                    ProduccionLecheItem(
                                        registro = produccion,
                                        onClick = { onNavigateToFormularioProduccionLeche(animalId, produccion.id) },
                                        onEliminar = { registroAEliminar = produccion.id }
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
                    is GanadoViewModel.ListaProduccionLecheState.Error -> {
                        val errorMessage = (produccionLecheState as GanadoViewModel.ListaProduccionLecheState.Error).mensaje
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
                                    onClick = { viewModel.cargarProduccionLeche(animalId) }
                                ) {
                                    Text("Reintentar")
                                }
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
            title = { Text("Eliminar Registro de Producción") },
            text = { Text("¿Estás seguro de que deseas eliminar este registro? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarProduccionLeche(registroId)
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
 * Componente para mostrar un registro de producción de leche individual en la lista.
 */
@Composable
fun ProduccionLecheItem(
    registro: ProduccionLeche,
    onClick: () -> Unit,
    onEliminar: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
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
            // Encabezado: Fecha y Cantidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dateFormat.format(registro.fecha),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Hora: ${timeFormat.format(registro.fecha)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Text(
                    text = "${registro.cantidad} litros",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Horario de ordeño
            Text(
                text = "Horario: ${registro.horario.name}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Calidad si está disponible
            if (registro.calidad.isNotBlank()) {
                Text(
                    text = "Calidad: ${registro.calidad}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Porcentajes si están disponibles
            registro.porcentajeGrasa?.let {
                Text(
                    text = "Grasa: $it%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            registro.porcentajeProteina?.let {
                Text(
                    text = "Proteína: $it%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Observaciones
            if (registro.observaciones.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Observaciones: ${registro.observaciones}",
                    style = MaterialTheme.typography.bodyMedium
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