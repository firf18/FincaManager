package com.example.fincamanager

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.fincamanager.navigation.GanadoRoutes
import com.example.fincamanager.navigation.Routes
import com.example.fincamanager.ui.auth.LoginScreen
import com.example.fincamanager.ui.auth.RegistrationScreen
import com.example.fincamanager.ui.ganado.screens.*
import com.example.fincamanager.ui.screens.*
import com.example.fincamanager.ui.theme.FincaManagerTheme
import com.example.fincamanager.util.LiveReloadServer
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import dagger.hilt.android.AndroidEntryPoint
import java.net.HttpURLConnection
import java.net.URL

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "FincaManager"
    }

    // Handler para las actualizaciones en vivo
    private val liveReloadHandler = Handler(Looper.getMainLooper())
    private var liveReloadRunnable: Runnable? = null
    private var liveReloadActive = false
    private var liveReloadServer: LiveReloadServer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar Firebase al inicio de la aplicación
        FirebaseApp.initializeApp(this)?.let {
            Log.d(TAG, "Firebase inicializado correctamente")

            // Habilitar el proveedor de depuración de App Check en modo desarrollo
            val firebaseAppCheck = FirebaseAppCheck.getInstance()
            if (BuildConfig.DEBUG) {
                firebaseAppCheck.installAppCheckProviderFactory(
                        DebugAppCheckProviderFactory.getInstance()
                )
                Log.d(TAG, "App Check en modo debug activado")
            }
        }

        // Iniciar el servidor de LiveReload si estamos en modo debug
        if (BuildConfig.DEBUG) {
            liveReloadServer = LiveReloadServer(this)
            setupLiveReload()
        }

        setContent { MainAppContent() }
    }

    private fun setupLiveReload() {
        liveReloadActive = true
        Log.d(TAG, "Iniciando servicio de Live Reload")

        liveReloadRunnable = Runnable {
            try {
                // Verificar cambios cada 1 segundo
                val url = URL("http://localhost:8081/status")
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 500
                connection.readTimeout = 500

                if (connection.responseCode == 200) {
                    // Si hay cambios detectados, recargamos la actividad
                    Log.d(TAG, "Cambios detectados, recargando la UI...")
                    recreate()
                }
            } catch (e: Exception) {
                // Ignorar errores de conexión
            }

            // Continuar verificando si el Live Reload sigue activo
            if (liveReloadActive) {
                liveReloadHandler.postDelayed(liveReloadRunnable!!, 1000)
            }
        }

        // Iniciar el ciclo de verificación
        liveReloadHandler.post(liveReloadRunnable!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener el ciclo de actualización en vivo
        liveReloadActive = false
        liveReloadRunnable?.let { liveReloadHandler.removeCallbacks(it) }
        liveReloadServer?.stop()
    }
}

@Composable
fun MainAppContent() {
    val navController = rememberNavController()

    // IMPORTANTE: Siempre empezar en Login para asegurar la autenticación correcta
    // y evitar problemas de navegación

    Scaffold { innerPadding ->
        NavHost(
                navController = navController,
                startDestination = Routes.Login.route,
                modifier = Modifier.padding(innerPadding)
        ) {
            // Pantallas de autenticación
            composable(Routes.Login.route) {
                Log.d("FincaManager", "Navegando a LoginScreen")
                LoginScreen(navController = navController)
            }

            composable(Routes.Registration.route) {
                Log.d("FincaManager", "Navegando a RegistrationScreen")
                RegistrationScreen(navController = navController)
            }

            // Pantallas principales - Con tema claro/oscuro según configuración
            composable(Routes.Home.route) {
                Log.d("FincaManager", "Navegando a HomeScreen")
                FincaManagerTheme { HomeScreen(navController = navController) }
            }

            composable(Routes.Ganado.route) {
                Log.d("FincaManager", "Navegando a SeleccionEspeciesScreen desde Routes.Ganado")
                FincaManagerTheme { 
                    SeleccionEspeciesScreen(navController = navController)
                }
            }

            // Rutas del módulo de ganado
            composable(GanadoRoutes.SELECCION_ESPECIES) {
                Log.d("FincaManager", "Navegando a SeleccionEspeciesScreen desde GanadoRoutes")
                FincaManagerTheme { SeleccionEspeciesScreen(navController = navController) }
            }

            // Dashboard de Ganado - Nueva pantalla
            composable(GanadoRoutes.DASHBOARD_GANADO) {
                Log.d("FincaManager", "Navegando a GanadoDashboardScreen")
                FincaManagerTheme { GanadoDashboardScreen(navController = navController) }
            }

            // Rutas adicionales del módulo de ganado
            composable(GanadoRoutes.LISTA_ANIMALES) {
                Log.d("FincaManager", "Navegando a GanadoListaAnimalesScreen")
                FincaManagerTheme { 
                    GanadoListaAnimalesScreen(
                        navController = navController,
                        onNavigateToDetalleAnimal = { animalId ->
                            navController.navigate(GanadoRoutes.detalleAnimal(animalId))
                        },
                        onNavigateToFormularioAnimal = {
                            navController.navigate(GanadoRoutes.formularioAnimal())
                        }
                    )
                }
            }

            // Ruta para detalle de animal
            composable(GanadoRoutes.DETALLE_ANIMAL) { backStackEntry ->
                val animalId = backStackEntry.arguments?.getString("animalId") ?: ""
                Log.d("FincaManager", "Navegando a AnimalDetalleScreen con ID: $animalId")
                FincaManagerTheme {
                    AnimalDetalleScreen(
                        navController = navController,
                        animalId = animalId,
                        onNavigateToEditar = { id ->
                            navController.navigate(GanadoRoutes.formularioAnimal(id))
                        },
                        onNavigateToRegistrosSanitarios = { id ->
                            navController.navigate(GanadoRoutes.registrosSanitarios(id))
                        },
                        onNavigateToProduccionLeche = { id ->
                            navController.navigate(GanadoRoutes.produccionLeche(id))
                        },
                        onNavigateToRegistrosReproduccion = { id ->
                            navController.navigate(GanadoRoutes.registrosReproduccion(id))
                        }
                    )
                }
            }
            
            // Ruta para formulario de animal (crear o editar)
            composable(
                route = GanadoRoutes.FORMULARIO_ANIMAL,
                arguments = listOf(navArgument("animalId") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val animalId = backStackEntry.arguments?.getString("animalId")
                FincaManagerTheme {
                    AnimalFormularioScreen(
                        navController = navController,
                        animalId = animalId
                    )
                }
            }

            composable(Routes.Cultivos.route) {
                Log.d("FincaManager", "Navegando a CultivosScreen")
                FincaManagerTheme { CultivosScreen(navController = navController) }
            }

            composable(Routes.Personal.route) {
                Log.d("FincaManager", "Navegando a PersonalScreen")
                FincaManagerTheme { PersonalScreen(navController = navController) }
            }

            composable(Routes.Inventario.route) {
                Log.d("FincaManager", "Navegando a InventarioScreen")
                FincaManagerTheme { InventarioScreen(navController = navController) }
            }

            composable(Routes.Finanzas.route) {
                Log.d("FincaManager", "Navegando a FinanzasScreen")
                FincaManagerTheme { FinanzasScreen(navController = navController) }
            }

            composable(Routes.Reportes.route) {
                Log.d("FincaManager", "Navegando a ReportesScreen")
                FincaManagerTheme { ReportesScreen(navController = navController) }
            }

            composable(Routes.Configuracion.route) {
                Log.d("FincaManager", "Navegando a ConfiguracionScreen")
                FincaManagerTheme { ConfiguracionScreen(navController = navController) }
            }
        }
    }
}
