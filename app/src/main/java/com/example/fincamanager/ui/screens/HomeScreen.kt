package com.example.fincamanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fincamanager.R
import com.example.fincamanager.navigation.Routes
import com.example.fincamanager.ui.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    // Obtener información del usuario
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val userName = currentUser?.displayName ?: currentUser?.email?.substringBefore('@') ?: "Usuario"

    // Determinar el saludo según la hora del día
    val greeting = getGreetingByTime()
    
    Scaffold(
        topBar = {
            // TopBar minimalista sin fondo de color
            TopAppBar(
                title = { },  // Sin título para un aspecto más limpio
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,  // Fondo transparente/igual al fondo
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start,  // Alineación a la izquierda para el saludo
            verticalArrangement = Arrangement.spacedBy(24.dp)  // Más espaciado entre elementos
        ) {
            // Cabecera con saludo personalizado
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "¿Qué quieres gestionar hoy?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            // Cuadrícula de tarjetas en forma de grid (2 columnas)
            // Primera fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuCard(
                    title = stringResource(id = R.string.module_livestock),
                    icon = Icons.Filled.Person,
                    onClick = { navController.navigate(Routes.Ganado.route) },
                    modifier = Modifier.weight(1f)
                )
                MenuCard(
                    title = stringResource(id = R.string.module_crops),
                    icon = Icons.Filled.Star,
                    onClick = { navController.navigate(Routes.Cultivos.route) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Segunda fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuCard(
                    title = stringResource(id = R.string.module_staff),
                    icon = Icons.Filled.Person,
                    onClick = { navController.navigate(Routes.Personal.route) },
                    modifier = Modifier.weight(1f)
                )
                MenuCard(
                    title = stringResource(id = R.string.module_inventory),
                    icon = Icons.AutoMirrored.Filled.List,
                    onClick = { navController.navigate(Routes.Inventario.route) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Tercera fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuCard(
                    title = stringResource(id = R.string.module_finances),
                    icon = Icons.Filled.Info,
                    onClick = { navController.navigate(Routes.Finanzas.route) },
                    modifier = Modifier.weight(1f)
                )
                MenuCard(
                    title = stringResource(id = R.string.module_reports),
                    icon = Icons.Filled.Menu,
                    onClick = { navController.navigate(Routes.Reportes.route) },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Botón de configuración centrado
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                MenuCard(
                    title = stringResource(id = R.string.module_settings),
                    icon = Icons.Filled.Settings,
                    onClick = { navController.navigate(Routes.Configuracion.route) },
                    modifier = Modifier.width(180.dp)  // Ancho fijo para centrar mejor
                )
            }
        }
    }
}

/**
 * Devuelve un saludo personalizado según la hora del día
 */
@Composable
private fun getGreetingByTime(): String {
    val calendar = Calendar.getInstance()
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    
    return when {
        hourOfDay < 12 -> stringResource(R.string.greeting_morning)
        hourOfDay < 18 -> stringResource(R.string.greeting_afternoon)
        else -> stringResource(R.string.greeting_evening)
    }
}

@Composable
fun MenuCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)  // Color más suave
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),  // Sin sombra
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))  // Borde más sutil
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary  // Color primario para los iconos
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant  // Color de texto más suave
            )
        }
    }
} 