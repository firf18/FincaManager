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
import com.example.fincamanager.data.model.ganado.RegistroReproduccion
import com.example.fincamanager.data.model.ganado.TipoEventoReproductivo
import com.example.fincamanager.ui.ganado.GanadoViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.HorizontalDivider

/**
 * Pantalla que muestra los registros reproductivos de un animal específico.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrosReproduccionScreen(
    navController: NavController,
    animalId: String,
    viewModel: GanadoViewModel = hiltViewModel(),
    onNavigateToFormularioRegistroReproduccion: (String, String?) -> Unit
) {
    // Colectar el estado de los registros reproductivos
    val registrosReproduccionState by viewModel.registrosReproduccionState.collectAsState()
    
    // Colectar el animal seleccionado
    val animalSeleccionado by viewModel.animalSeleccionado.collectAsState()
    
    // Colectar el mensaje de operación
    val mensajeOperacion by viewModel.mensajeOperacion.collectAsState()
    
    // Mostrar snackbar con mensajes
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Variable para controlar el diálogo de confirmación de eliminación
    var registroAEliminar by remember { mutableStateOf<String?>(null) }
    
    // Cargar el animal y sus registros reproductivos
    LaunchedEffect(animalId) {
        viewModel.cargarAnimal(animalId)
        viewModel.cargarRegistrosReproduccion(animalId)
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
                        text = "Registros Reproductivos" + 
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
                onClick = { onNavigateToFormularioRegistroReproduccion(animalId, null) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar registro reproductivo"
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
            when (registrosReproduccionState) {
                is GanadoViewModel.ListaRegistrosReproduccionState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is GanadoViewModel.ListaRegistrosReproduccionState.Success -> {
                    val registros = (registrosReproduccionState as GanadoViewModel.ListaRegistrosReproduccionState.Success).registros
                    if (registros.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay registros reproductivos para este animal",
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
                                RegistroReproduccionItem(
                                    registro = registro,
                                    onClick = { onNavigateToFormularioRegistroReproduccion(animalId, registro.id) },
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
                is GanadoViewModel.ListaRegistrosReproduccionState.Error -> {
                    val errorMessage = (registrosReproduccionState as GanadoViewModel.ListaRegistrosReproduccionState.Error).mensaje
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
                                onClick = { viewModel.cargarRegistrosReproduccion(animalId) }
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
            title = { Text("Eliminar Registro Reproductivo") },
            text = { Text("¿Estás seguro de que deseas eliminar este registro? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarRegistroReproduccion(registroId)
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
 * Componente para mostrar un registro reproductivo individual en la lista.
 */
@Composable
fun RegistroReproduccionItem(
    registro: RegistroReproduccion,
    onClick: () -> Unit,
    onEliminar: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    // Determinar el color según el tipo de evento
    val chipColor = when (registro.tipoEvento) {
        TipoEventoReproductivo.CELO -> MaterialTheme.colorScheme.tertiary
        TipoEventoReproductivo.INSEMINACION -> MaterialTheme.colorScheme.primary
        TipoEventoReproductivo.DIAGNOSTICO_GESTACION -> MaterialTheme.colorScheme.secondary
        TipoEventoReproductivo.MONTA -> MaterialTheme.colorScheme.primary
        TipoEventoReproductivo.PARTO -> MaterialTheme.colorScheme.error
        TipoEventoReproductivo.ABORTO -> MaterialTheme.colorScheme.error
        TipoEventoReproductivo.OTRO -> MaterialTheme.colorScheme.outline
    }
    
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
            // Encabezado: Fecha y Tipo de evento
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
                
                SuggestionChip(
                    onClick = { },
                    label = { Text(registro.tipoEvento.name) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = chipColor.copy(alpha = 0.2f),
                        labelColor = chipColor
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Descripción basada en el tipo de evento
            when (registro.tipoEvento) {
                TipoEventoReproductivo.INSEMINACION -> {
                    if (registro.inseminador.isNotBlank()) {
                        Text(
                            text = "Inseminador: ${registro.inseminador}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (registro.tipoSemen.isNotBlank()) {
                        Text(
                            text = "Tipo de semen: ${registro.tipoSemen}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                TipoEventoReproductivo.PARTO -> {
                    Text(
                        text = "Crías: ${registro.cantidadCrias}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (registro.idsCrias.isNotEmpty()) {
                        Text(
                            text = "IDs de crías: ${registro.idsCrias.joinToString(", ")}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                TipoEventoReproductivo.DIAGNOSTICO_GESTACION -> {
                    registro.fechaProbableParto?.let {
                        Text(
                            text = "Fecha probable de parto: ${dateFormat.format(it)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Resultado: ${if (registro.resultado == true) "Positivo" else "Negativo"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                else -> {}
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