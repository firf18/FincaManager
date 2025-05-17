package com.example.fincamanager.di

import android.content.Context
import androidx.room.Room
import com.example.fincamanager.data.local.AppDatabase
import com.example.fincamanager.data.local.dao.AnimalDao
import com.example.fincamanager.data.local.dao.ProduccionLecheDao
import com.example.fincamanager.data.local.dao.RegistroReproduccionDao
import com.example.fincamanager.data.local.dao.RegistroSanitarioDao
import com.example.fincamanager.data.repository.GanadoRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFincaManagerDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "finca_manager_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideAnimalDao(database: AppDatabase): AnimalDao {
        return database.animalDao()
    }

    @Provides
    @Singleton
    fun provideRegistroSanitarioDao(database: AppDatabase): RegistroSanitarioDao {
        return database.registroSanitarioDao()
    }

    @Provides
    @Singleton
    fun provideProduccionLecheDao(database: AppDatabase): ProduccionLecheDao {
        return database.produccionLecheDao()
    }

    @Provides
    @Singleton
    fun provideRegistroReproduccionDao(database: AppDatabase): RegistroReproduccionDao {
        return database.registroReproduccionDao()
    }
    
    // Note: We'll need to implement GanadoRepositoryImpl later
    // For now, we're removing it as it doesn't exist yet
} 