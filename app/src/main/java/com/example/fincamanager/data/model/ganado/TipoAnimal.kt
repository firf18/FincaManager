package com.example.fincamanager.data.model.ganado

/**
 * Enumeración que representa los tipos de animales en el sistema.
 */
enum class TipoAnimal {
    BOVINO,
    OVINO,
    CAPRINO,
    PORCINO,
    EQUINO,
    AVIAR,
    OTRO
}

/**
 * Enumeración que representa el sexo del animal.
 */
enum class Sexo {
    MACHO,
    HEMBRA
}

/**
 * Enumeración que representa el estado reproductivo de un animal.
 */
enum class EstadoReproductivo {
    NO_REPRODUCTIVO,
    DISPONIBLE,
    GESTANTE,
    LACTANTE,
    SERVICIO,
    DESCANSO
} 