package com.example.fincamanager.navigation

/**
 * Rutas de navegación para la aplicación FincaManager
 * Usando sealed class para garantizar type safety en la navegación
 */
sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Registration : Routes("registration")
    object Home : Routes("home")
    object Ganado : Routes("ganado")
    object Cultivos : Routes("cultivos")
    object Personal : Routes("personal")
    object Inventario : Routes("inventario")
    object Finanzas : Routes("finanzas")
    object Reportes : Routes("reportes")
    object Configuracion : Routes("configuracion")
}

// Rutas para el módulo de ganado
object GanadoRoutes {
    const val SELECCION_ESPECIES = "ganado/especies"
    const val DASHBOARD_GANADO = "ganado/dashboard"
    const val LISTA_ANIMALES = "ganado/lista"
    const val DETALLE_ANIMAL = "ganado/animal/{animalId}"
    const val FORMULARIO_ANIMAL = "ganado/animal/edit?animalId={animalId}"
    const val REGISTROS_SANITARIOS = "ganado/animal/{animalId}/sanitarios"
    const val FORMULARIO_REGISTRO_SANITARIO = "ganado/animal/{animalId}/sanitarios/edit?registroId={registroId}"
    const val PRODUCCION_LECHE = "ganado/animal/{animalId}/leche"
    const val FORMULARIO_PRODUCCION_LECHE = "ganado/animal/{animalId}/leche/edit?registroId={registroId}"
    const val REGISTROS_REPRODUCCION = "ganado/animal/{animalId}/reproduccion"
    const val FORMULARIO_REGISTRO_REPRODUCCION = "ganado/animal/{animalId}/reproduccion/edit?registroId={registroId}"
    
    // Funciones de navegación con parámetros
    fun detalleAnimal(animalId: String) = "ganado/animal/$animalId"
    fun formularioAnimal(animalId: String? = null) = if (animalId != null) "ganado/animal/edit?animalId=$animalId" else "ganado/animal/edit"
    fun registrosSanitarios(animalId: String) = "ganado/animal/$animalId/sanitarios"
    fun formularioRegistroSanitario(animalId: String, registroId: String? = null) = 
        if (registroId != null) "ganado/animal/$animalId/sanitarios/edit?registroId=$registroId" else "ganado/animal/$animalId/sanitarios/edit"
    fun produccionLeche(animalId: String) = "ganado/animal/$animalId/leche"
    fun formularioProduccionLeche(animalId: String, registroId: String? = null) = 
        if (registroId != null) "ganado/animal/$animalId/leche/edit?registroId=$registroId" else "ganado/animal/$animalId/leche/edit"
    fun registrosReproduccion(animalId: String) = "ganado/animal/$animalId/reproduccion"
    fun formularioRegistroReproduccion(animalId: String, registroId: String? = null) = 
        if (registroId != null) "ganado/animal/$animalId/reproduccion/edit?registroId=$registroId" else "ganado/animal/$animalId/reproduccion/edit"
} 