package com.example.fincamanager.ui.ganado.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fincamanager.R
import com.example.fincamanager.data.model.ganado.Animal
import com.example.fincamanager.ui.ganado.GanadoViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.HorizontalDivider

/**
 * Pantalla que muestra los detalles de un animal específico.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetalleScreen(
    navController: NavController,
    animalId: String,
    viewModel: GanadoViewModel = hiltViewModel(),
    onNavigateToEditar: (String) -> Unit,
    onNavigateToRegistrosSanitarios: (String) -> Unit,
    onNavigateToProduccionLeche: (String) -> Unit,
    onNavigateToRegistrosReproduccion: (String) -> Unit
) {
    // Estado del animal seleccionado
    val animalSeleccionado by viewModel.animalSeleccionado.collectAsState()
    
    // Mensaje de operación
    val mensajeOperacion by viewModel.mensajeOperacion.collectAsState()
    
    // Diálogo de confirmación para eliminar
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    
    // Snackbar para mensajes
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Cargar el animal cuando cambia el ID
    LaunchedEffect(animalId) {
        viewModel.getAnimalById(animalId)
    }
    
    // Mostrar mensajes
    LaunchedEffect(mensajeOperacion) {
        mensajeOperacion?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMensajeOperacion()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Animal") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEditar(animalId) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar"
                        )
                    }
                    IconButton(onClick = { mostrarDialogoEliminar = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Contenido según el estado del animal
            if (animalSeleccionado == null) {
                // Cargando o no encontrado
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Mostrar detalles del animal
                DetalleAnimalContenido(
                    animal = animalSeleccionado!!,
                    onRegistrosSanitarios = { onNavigateToRegistrosSanitarios(animalId) },
                    onProduccionLeche = { onNavigateToProduccionLeche(animalId) },
                    onRegistrosReproduccion = { onNavigateToRegistrosReproduccion(animalId) }
                )
            }
        }
    }
    
    // Diálogo de confirmación para eliminar
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar Animal") },
            text = { Text("¿Estás seguro de que deseas eliminar este animal? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarAnimal(animalId)
                        mostrarDialogoEliminar = false
                        navController.navigateUp()
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoEliminar = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Contenido principal de la pantalla de detalle de animal.
 */
@Composable
fun DetalleAnimalContenido(
    animal: Animal,
    onRegistrosSanitarios: () -> Unit,
    onProduccionLeche: () -> Unit,
    onRegistrosReproduccion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Encabezado con nombre y estado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (animal.nombre.isNotBlank()) animal.nombre else "Animal sin nombre",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            EstadoAnimalChip(estado = animal.estado)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Identificación
        Text(
            text = "ID: ${animal.identificacion}",
            style = MaterialTheme.typography.titleMedium
        )
        
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
        
        // Información general
        Text(
            text = "Información General",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Datos básicos
        DetalleItem("Especie", animal.especie)
        DetalleItem("Raza", animal.raza)
        DetalleItem("Género", animal.genero)
        
        animal.fechaNacimiento?.let {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            DetalleItem("Fecha de nacimiento", formatter.format(it))
        }
        
        DetalleItem("Peso", "${animal.peso} kg")
        
        if (animal.color.isNotBlank()) {
            DetalleItem("Color", animal.color)
        }
        
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
        
        // Información de origen
        Text(
            text = "Información de Origen",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        DetalleItem("Procedencia", animal.procedencia)
        
        if (animal.idMadre.isNotBlank()) {
            DetalleItem("ID de la madre", animal.idMadre)
        }
        
        if (animal.idPadre.isNotBlank()) {
            DetalleItem("ID del padre", animal.idPadre)
        }
        
        animal.fechaAdquisicion?.let {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            DetalleItem("Fecha de adquisición", formatter.format(it))
        }
        
        if (animal.precioAdquisicion > 0) {
            DetalleItem("Precio de adquisición", "$${animal.precioAdquisicion}")
        }
        
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
        
        // Módulos relacionados
        Text(
            text = "Registros Relacionados",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Botones para acceder a registros
        ModuloRelacionadoBoton(
            titulo = "Registros Sanitarios",
            descripcion = "Vacunas, tratamientos, y diagnósticos de salud",
            icono = Icons.Default.HealthAndSafety,
            onClick = onRegistrosSanitarios
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ModuloRelacionadoBoton(
            titulo = "Producción de Leche",
            descripcion = "Registros de ordeño y producción diaria",
            icono = Icons.Default.LocalDrink,
            onClick = onProduccionLeche
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ModuloRelacionadoBoton(
            titulo = "Reproducción",
            descripcion = "Celos, inseminaciones, gestaciones y partos",
            icono = Icons.Default.Timeline,
            onClick = onRegistrosReproduccion
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * Componente para mostrar un par clave-valor de información.
 */
@Composable
fun DetalleItem(label: String, value: String) {
    if (value.isBlank()) return
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Botón para acceder a un módulo relacionado con el animal.
 */
@Composable
fun ModuloRelacionadoBoton(
    titulo: String,
    descripcion: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver"
            )
        }
    }
} 