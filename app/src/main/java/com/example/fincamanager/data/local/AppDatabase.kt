package com.example.fincamanager.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fincamanager.data.Converters
import com.example.fincamanager.data.local.dao.*
import com.example.fincamanager.data.model.User
import com.example.fincamanager.data.model.ganado.*

@Database(
    entities = [
        User::class,
        // Entidades de gestión ganadera
        Animal::class,
        RegistroSanitario::class,
        ProduccionLeche::class,
        RegistroReproduccion::class
        // Otras entidades que serán añadidas después
        // Cultivo::class, ActividadCultivo::class, Empleado::class, Tarea::class, Inventario::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // User DAO
    abstract fun userDao(): UserDao
    
    // DAOs de gestión ganadera
    abstract fun animalDao(): AnimalDao
    abstract fun registroSanitarioDao(): RegistroSanitarioDao
    abstract fun produccionLecheDao(): ProduccionLecheDao
    abstract fun registroReproduccionDao(): RegistroReproduccionDao
    
    // DAOs que serán añadidos después
    // abstract fun cultivoDao(): CultivoDao
    // abstract fun actividadCultivoDao(): ActividadCultivoDao
    // abstract fun empleadoDao(): EmpleadoDao
    // abstract fun tareaDao(): TareaDao
    // abstract fun inventarioDao(): InventarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finca_manager_db"
                )
                // .addMigrations(MIGRATION_1_2) // Añadir migraciones si es necesario
                .fallbackToDestructiveMigration() // Opción simple para desarrollo, ¡cuidado en producción!
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 