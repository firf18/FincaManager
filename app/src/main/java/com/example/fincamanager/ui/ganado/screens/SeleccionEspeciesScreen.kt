package com.example.fincamanager.ui.ganado.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fincamanager.R
import com.example.fincamanager.navigation.GanadoRoutes
import com.example.fincamanager.navigation.Routes
import com.example.fincamanager.ui.ganado.GanadoViewModel
import com.example.fincamanager.ui.theme.PrimaryGreen

/**
 * Pantalla que permite al usuario seleccionar las especies de animales que desea gestionar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionEspeciesScreen(
    navController: NavController,
    viewModel: GanadoViewModel = hiltViewModel()
) {
    val especies by viewModel.especiesDisponibles.collectAsState()
    val especiesSeleccionadas by viewModel.especiesSeleccionadas.collectAsState()
    
    // Variable para determinar si venimos del dashboard o no
    // Podríamos mejorarlo accediendo a la ruta anterior, pero esta es una solución simple
    val mostrarDashboard = remember { mutableStateOf(false) }
    
    // Comprobamos las rutas previas para determinar de dónde venimos
    LaunchedEffect(Unit) {
        val prevBackStackEntry = navController.previousBackStackEntry
        if (prevBackStackEntry?.destination?.route == GanadoRoutes.DASHBOARD_GANADO) {
            mostrarDashboard.value = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selección de Animales") },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (mostrarDashboard.value) {
                            // Si venimos del dashboard, volvemos a él
                            navController.navigate(GanadoRoutes.DASHBOARD_GANADO) {
                                popUpTo(GanadoRoutes.DASHBOARD_GANADO) { inclusive = true }
                            }
                        } else {
                            // De lo contrario, volvemos al home
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.Home.route) { inclusive = false }
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { viewModel.limpiarEspeciesSeleccionadas() }
                    ) {
                        Text("Limpiar selección")
                    }
                    
                    Button(
                        onClick = {
                            try {
                                // Cargar los animales filtrados primero
                                viewModel.cargarAnimalesFiltrados()
                                // Después navegar al dashboard de ganado
                                navController.navigate(GanadoRoutes.DASHBOARD_GANADO) {
                                    // Si venimos del dashboard, reemplazamos esa entrada en el backstack
                                    if (mostrarDashboard.value) {
                                        popUpTo(GanadoRoutes.DASHBOARD_GANADO) { inclusive = true }
                                    }
                                    launchSingleTop = true
                                }
                            } catch (e: Exception) {
                                // En caso de error, registramos el error pero evitamos el cierre de la app
                                e.printStackTrace()
                            }
                        },
                        enabled = especiesSeleccionadas.isNotEmpty(),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Continuar")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Continuar"
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Qué animales quieres gestionar?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Selecciona uno o más tipos de animales para trabajar con ellos",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(especies) { especie ->
                    val isSelected = especiesSeleccionadas.contains(especie)
                    AnimalImageButton(
                        especie = especie,
                        isSelected = isSelected,
                        onToggle = { viewModel.toggleEspecieSeleccion(especie) }
                    )
                }
            }
        }
    }
}

/**
 * Botón de imagen para selección de animales, similar al ejemplo de Animal Sounds.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalImageButton(
    especie: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val imageResource = when (especie.lowercase()) {
        "vaca" -> R.drawable.ic_cow
        "caballo" -> R.drawable.ic_horse
        "toro" -> R.drawable.ic_bull
        "búfalo" -> R.drawable.ic_buffalo
        "cerdo" -> R.drawable.ic_pig
        "gallina ponedora" -> R.drawable.ic_hen
        "pollo de engorde" -> R.drawable.ic_chicken
        "pato" -> R.drawable.ic_duck
        "ganso" -> R.drawable.ic_goose
        "abeja" -> R.drawable.ic_bee
        "pez" -> R.drawable.ic_fish
        // Usar ícono por defecto para los demás
        else -> R.drawable.ic_animal_default
    }

    Card(
        onClick = onToggle,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) PrimaryGreen else Color.LightGray
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                // Icono o imagen del animal
                Image(
                    painter = painterResource(id = imageResource),
                    contentDescription = especie,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(bottom = 8.dp),
                    contentScale = ContentScale.Fit
                )
                
                // Nombre del animal
                Text(
                    text = especie,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = if (isSelected) PrimaryGreen else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                // Indicador de selección
                if (isSelected) {
                    Surface(
                        color = PrimaryGreen,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Seleccionado",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
} 