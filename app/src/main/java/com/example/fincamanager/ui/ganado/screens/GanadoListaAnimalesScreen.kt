package com.example.fincamanager.ui.ganado.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fincamanager.R
import com.example.fincamanager.data.model.ganado.Animal
import com.example.fincamanager.data.model.ganado.EstadoAnimal
import com.example.fincamanager.navigation.GanadoRoutes
import com.example.fincamanager.navigation.Routes
import com.example.fincamanager.ui.ganado.GanadoViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Pantalla que muestra la lista de animales registrados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GanadoListaAnimalesScreen(
    navController: NavController,
    viewModel: GanadoViewModel = hiltViewModel(),
    onNavigateToDetalleAnimal: (String) -> Unit,
    onNavigateToFormularioAnimal: () -> Unit
) {
    // Debug log
    Log.d("FincaManager", "Entrando a GanadoListaAnimalesScreen")
    
    // Estado de búsqueda
    var searchQuery by remember { mutableStateOf("") }
    
    // Colectar el estado de la lista de animales
    val animalesState by viewModel.animalesState.collectAsState()
    
    // Obtener la especie actual seleccionada para filtrar
    val filtroEspecie by viewModel.filtroEspecie.collectAsState()
    
    // Colectar el mensaje de operación
    val mensajeOperacion by viewModel.mensajeOperacion.collectAsState()
    
    // Mostrar snackbar con mensajes
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Realizar la carga inicial de forma segura
    LaunchedEffect(Unit) {
        try {
            Log.d("FincaManager", "Cargando animales en GanadoListaAnimalesScreen")
            viewModel.cargarAnimalesFiltrados() 
        } catch (e: Exception) {
            Log.e("FincaManager", "Error al cargar animales filtrados", e)
        }
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
                title = { Text(stringResource(R.string.module_livestock)) },
                navigationIcon = {
                    IconButton(onClick = { 
                        // Navegación mejorada: volver al dashboard de ganado en lugar de la pantalla de especies
                        navController.navigate(GanadoRoutes.DASHBOARD_GANADO) {
                            // Limpiar todas las entradas hasta el dashboard
                            popUpTo(GanadoRoutes.DASHBOARD_GANADO)
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar al dashboard"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                try {
                    // Crear una copia del animal con la especie actual si existe
                    filtroEspecie?.let { especie ->
                        viewModel.limpiarFormularioAnimal()
                        viewModel.actualizarCampoFormularioAnimal("especie", especie)
                    }
                    onNavigateToFormularioAnimal()
                } catch (e: Exception) {
                    Log.e("FincaManager", "Error al navegar al formulario de animal", e)
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar animal"
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
            // Indicador de especie filtrada
            filtroEspecie?.let { especie ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Especie filtrada: $especie",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            viewModel.setFiltroEspecie("")
                            viewModel.cargarAnimales()
                        }
                    ) {
                        Text("Quitar filtro")
                    }
                }
            }
            
            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar animal por identificación o nombre") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                singleLine = true
            )
            
            // Contenido principal según el estado
            when (animalesState) {
                is GanadoViewModel.ListaAnimalesState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is GanadoViewModel.ListaAnimalesState.Success -> {
                    val animales = (animalesState as GanadoViewModel.ListaAnimalesState.Success).animales
                    
                    // Filtrar animales según búsqueda
                    val animalesFiltrados = if (searchQuery.isBlank()) {
                        animales
                    } else {
                        animales.filter { 
                            it.identificacion.contains(searchQuery, ignoreCase = true) ||
                            it.nombre.contains(searchQuery, ignoreCase = true)
                        }
                    }
                    
                    if (animalesFiltrados.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isBlank()) "No hay animales registrados" else "No se encontraron resultados",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(animalesFiltrados) { animal ->
                                AnimalListItem(
                                    animal = animal,
                                    onClick = { onNavigateToDetalleAnimal(animal.id) }
                                )
                            }
                        }
                    }
                }
                is GanadoViewModel.ListaAnimalesState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = (animalesState as GanadoViewModel.ListaAnimalesState.Error).mensaje,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.cargarAnimales() }
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

/**
 * Componente que representa un elemento individual en la lista de animales.
 */
@Composable
fun AnimalListItem(
    animal: Animal,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (animal.nombre.isNotBlank()) animal.nombre else "Sin nombre",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ID: ${animal.identificacion}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                
                // Chip para el estado
                EstadoAnimalChip(estado = animal.estado)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Información básica
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Especie: ${animal.especie}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (animal.raza.isNotBlank()) {
                        Text(
                            text = "Raza: ${animal.raza}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                // Fecha de nacimiento/edad
                Column(horizontalAlignment = Alignment.End) {
                    animal.fechaNacimiento?.let { fecha ->
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        Text(
                            text = "Nacimiento: ${formatter.format(fecha)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (animal.peso > 0) {
                        Text(
                            text = "Peso: ${animal.peso} kg",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

/**
 * Chip que representa el estado de un animal con un color específico.
 */
@Composable
fun EstadoAnimalChip(estado: EstadoAnimal) {
    val (color, contentColor) = when (estado) {
        EstadoAnimal.ACTIVO -> Pair(Color(0xFF43A047), Color.White)
        EstadoAnimal.VENDIDO -> Pair(Color(0xFF1E88E5), Color.White)
        EstadoAnimal.FALLECIDO -> Pair(Color(0xFFE53935), Color.White)
        EstadoAnimal.SACRIFICADO -> Pair(Color(0xFFFF9800), Color.Black)
        EstadoAnimal.TRANSFERIDO -> Pair(Color(0xFF8E24AA), Color.White)
        EstadoAnimal.OTRO -> Pair(Color(0xFF757575), Color.White)
    }
    
    Surface(
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = estado.name,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
} 