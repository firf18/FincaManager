package com.example.fincamanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.annotation.NonNull

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    @NonNull
    val userId: Int = 0,

    @ColumnInfo(name = "email")
    @NonNull
    val email: String,

    @ColumnInfo(name = "password_hash")
    @NonNull
    val passwordHash: String,

    @ColumnInfo(name = "first_name")
    val firstName: String? = null, // Opcional

    @ColumnInfo(name = "last_name")
    val lastName: String? = null // Opcional
) 