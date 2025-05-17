package com.example.fincamanager.ui.ganado.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fincamanager.navigation.GanadoRoutes
import com.example.fincamanager.navigation.Routes
import com.example.fincamanager.ui.components.AnimalTypeCard
import com.example.fincamanager.ui.ganado.GanadoViewModel

/**
 * Pantalla que muestra un dashboard de los tipos de animales seleccionados
 * y permite navegar a la gestión de cada tipo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GanadoDashboardScreen(
    navController: NavController,
    viewModel: GanadoViewModel = hiltViewModel()
) {
    val especiesSeleccionadas by viewModel.especiesSeleccionadas.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard de Ganado") },
                navigationIcon = {
                    IconButton(onClick = { 
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Home.route) { inclusive = false }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al inicio"
                        )
                    }
                },
                actions = {
                    // Botón para añadir más tipos de animales
                    IconButton(onClick = {
                        navController.navigate(GanadoRoutes.SELECCION_ESPECIES) {
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir tipos de animales"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (especiesSeleccionadas.isEmpty()) {
            // Mostrar mensaje si no hay especies seleccionadas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No hay tipos de animales seleccionados",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            navController.navigate(GanadoRoutes.SELECCION_ESPECIES) {
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Text("Seleccionar tipos de animales")
                    }
                }
            }
        } else {
            // Mostrar cuadrícula de tipos de animales
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Gestión de animales por tipo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(especiesSeleccionadas.toList()) { especie ->
                        AnimalTypeCard(
                            animalType = especie,
                            onClick = {
                                // Navegar a la lista de animales de este tipo
                                viewModel.setFiltroEspecie(especie)
                                navController.navigate(GanadoRoutes.LISTA_ANIMALES) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
} 