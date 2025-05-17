package com.example.fincamanager.ui.cultivo.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fincamanager.data.model.cultivo.Cultivo
import com.example.fincamanager.data.model.cultivo.EstadoCultivo
import com.example.fincamanager.ui.cultivo.CultivoViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.graphics.Color

/**
 * Pantalla que muestra la lista de cultivos registrados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCultivosScreen(
    onNavigateToDetalleCultivo: (String) -> Unit,
    onNavigateToFormularioCultivo: (String?) -> Unit,
    viewModel: CultivoViewModel = hiltViewModel()
) {
    val cultivosState by viewModel.cultivosState.collectAsState()
    val mensajeOperacion by viewModel.mensajeOperacion.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Variable para controlar el diálogo de confirmación de eliminación
    var cultivoAEliminar by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        viewModel.cargarCultivos("fincaActual") // En un caso real, se debería obtener el ID de la finca actual
    }
    
    LaunchedEffect(mensajeOperacion) {
        mensajeOperacion?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMensajeOperacion()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cultivos") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToFormularioCultivo(null) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar cultivo"
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
            when (cultivosState) {
                is CultivoViewModel.ListaCultivosState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is CultivoViewModel.ListaCultivosState.Success -> {
                    val cultivos = (cultivosState as CultivoViewModel.ListaCultivosState.Success).cultivos
                    if (cultivos.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay cultivos registrados",
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
                            items(cultivos) { cultivo ->
                                CultivoItem(
                                    cultivo = cultivo,
                                    onClick = { onNavigateToDetalleCultivo(cultivo.id) },
                                    onEliminar = { cultivoAEliminar = cultivo.id }
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
                is CultivoViewModel.ListaCultivosState.Error -> {
                    val errorMessage = (cultivosState as CultivoViewModel.ListaCultivosState.Error).mensaje
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
                                onClick = { viewModel.cargarCultivos("fincaActual") }
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
    cultivoAEliminar?.let { cultivoId ->
        AlertDialog(
            onDismissRequest = { cultivoAEliminar = null },
            title = { Text("Eliminar Cultivo") },
            text = { Text("¿Estás seguro de que deseas eliminar este cultivo? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarCultivo(cultivoId)
                        cultivoAEliminar = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { cultivoAEliminar = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Componente para mostrar un cultivo individual en la lista.
 */
@Composable
fun CultivoItem(
    cultivo: Cultivo,
    onClick: () -> Unit,
    onEliminar: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    // Determinar el color según el estado del cultivo
    val chipColor = when (cultivo.estado) {
        EstadoCultivo.SEMBRADO -> MaterialTheme.colorScheme.primary
        EstadoCultivo.EN_CRECIMIENTO -> MaterialTheme.colorScheme.secondary
        EstadoCultivo.FLORECIMIENTO -> MaterialTheme.colorScheme.tertiary
        EstadoCultivo.FRUCTIFICACION -> MaterialTheme.colorScheme.error
        EstadoCultivo.MADURACION -> MaterialTheme.colorScheme.error
        EstadoCultivo.COSECHADO -> MaterialTheme.colorScheme.tertiary
        EstadoCultivo.ABANDONADO -> MaterialTheme.colorScheme.outline
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
            // Encabezado: Nombre y Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cultivo.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                SuggestionChip(
                    onClick = { },
                    label = { Text(cultivo.estado.name) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = chipColor.copy(alpha = 0.2f),
                        labelColor = chipColor
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Variedad
            if (cultivo.variedad.isNotBlank()) {
                Text(
                    text = "Variedad: ${cultivo.variedad}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            // Fechas de siembra y cosecha estimada
            Text(
                text = "Sembrado: ${dateFormat.format(cultivo.fechaSiembra)}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            cultivo.fechaCosechaEstimada?.let {
                Text(
                    text = "Cosecha estimada: ${dateFormat.format(it)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Área y ubicación
            Text(
                text = "Área: ${cultivo.areaEnHectareas} hectáreas",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Ubicación: ${cultivo.ubicacionLote}",
                style = MaterialTheme.typography.bodyMedium
            )
            
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