package com.example.fincamanager.ui.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.fincamanager.R

class AuthViewModel : ViewModel() {
    companion object {
        private const val TAG = "AuthViewModel"
    }
    
    private val auth = FirebaseAuth.getInstance()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState
    
    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser
    
    // Flujo para manejar el estado del restablecimiento de contraseña
    private val _passwordResetState = MutableStateFlow<PasswordResetState>(PasswordResetState.Initial)
    val passwordResetState: StateFlow<PasswordResetState> = _passwordResetState
    
    // Nuevo flujo para manejar errores específicos en validación
    private val _validationState = MutableStateFlow<ValidationState>(ValidationState.Valid)
    val validationState: StateFlow<ValidationState> = _validationState
    
    init {
        // Comprobar si hay un usuario ya autenticado
        Log.d(TAG, "Inicializando AuthViewModel, verificando usuario actual")
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            Log.d(TAG, "Estado de autenticación cambiado: ${firebaseAuth.currentUser?.email ?: "No hay usuario"}")
        }
    }
    
    fun signIn(email: String, password: String) {
        Log.d(TAG, "Intento de inicio de sesión: $email")
        // Validar entrada primero
        if (!isValidEmail(email)) {
            _validationState.value = ValidationState.InvalidEmail
            Log.e(TAG, "Email inválido: $email")
            return
        }
        
        if (!isValidPassword(password)) {
            _validationState.value = ValidationState.InvalidPassword
            Log.e(TAG, "Contraseña inválida")
            return
        }
        
        _validationState.value = ValidationState.Valid
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            Log.d(TAG, "Iniciando autenticación con email: $email")
            
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                Log.d(TAG, "Autenticación exitosa")
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                Log.e(TAG, "Error de autenticación: ${e.message}", e)
                _authState.value = AuthState.Error(parseFirebaseAuthError(e.message ?: ""))
            }
        }
    }
    
    fun signUp(email: String, password: String, confirmPassword: String) {
        Log.d(TAG, "Intento de registro: $email")
        // Validar entrada primero
        if (!isValidEmail(email)) {
            _validationState.value = ValidationState.InvalidEmail
            Log.e(TAG, "Email inválido: $email")
            return
        }
        
        if (!isValidPassword(password)) {
            _validationState.value = ValidationState.InvalidPassword
            Log.e(TAG, "Contraseña inválida")
            return
        }
        
        if (password != confirmPassword) {
            _validationState.value = ValidationState.PasswordsDoNotMatch
            Log.e(TAG, "Las contraseñas no coinciden")
            return
        }
        
        _validationState.value = ValidationState.Valid
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            Log.d(TAG, "Iniciando registro con email: $email")
            
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                Log.d(TAG, "Registro exitoso")
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                Log.e(TAG, "Error de registro: ${e.message}", e)
                _authState.value = AuthState.Error(parseFirebaseAuthError(e.message ?: ""))
            }
        }
    }
    
    fun signOut() {
        Log.d(TAG, "Cerrando sesión")
        auth.signOut()
        _authState.value = AuthState.Initial
    }
    
    fun resetPassword(email: String) {
        Log.d(TAG, "Intento de restablecer contraseña: $email")
        if (!isValidEmail(email)) {
            _validationState.value = ValidationState.InvalidEmail
            Log.e(TAG, "Email inválido: $email")
            return
        }
        
        _validationState.value = ValidationState.Valid
        viewModelScope.launch {
            _passwordResetState.value = PasswordResetState.Loading
            
            try {
                auth.sendPasswordResetEmail(email).await()
                Log.d(TAG, "Email de restablecimiento enviado")
                _passwordResetState.value = PasswordResetState.Success
            } catch (e: Exception) {
                Log.e(TAG, "Error al enviar email de restablecimiento: ${e.message}", e)
                _passwordResetState.value = PasswordResetState.Error(parseFirebaseAuthError(e.message ?: ""))
            }
        }
    }
    
    // Método para iniciar el flujo de autenticación con Google
    fun signInWithGoogle(context: Context) {
        Log.d(TAG, "Iniciando autenticación con Google")
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                getGoogleCredentials(context)
            } catch (e: Exception) {
                Log.e(TAG, "Error al iniciar sesión con Google: ${e.message}", e)
                _authState.value = AuthState.Error("Error al iniciar sesión con Google: ${e.message}")
            }
        }
    }
    
    // Método para solicitar credenciales usando CredentialManager
    private suspend fun getGoogleCredentials(context: Context) {
        try {
            Log.d(TAG, "Solicitando credenciales de Google")
            val credentialManager = CredentialManager.create(context)
            
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build()
            
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            
            Log.d(TAG, "Ejecutando solicitud de credencial")
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )
            
            Log.d(TAG, "Respuesta de credencial recibida")
            handleCredentialResponse(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener credenciales: ${e.message}", e)
            // Simplificamos el manejo de errores para evitar problemas con las subclases
            when {
                e.message?.contains("No credential") == true -> {
                    // Posible NoCredentialException
                    Log.w(TAG, "No hay credenciales disponibles")
                    _authState.value = AuthState.Error("Autenticación cancelada o no hay credenciales disponibles")
                }
                e.message?.contains("interrupt") == true -> {
                    // Posible InterruptedException
                    Log.w(TAG, "Autenticación interrumpida")
                    _authState.value = AuthState.Error("La autenticación fue interrumpida")
                }
                e.message?.contains("cancel") == true -> {
                    // Posible CancellationException
                    Log.w(TAG, "Autenticación cancelada")
                    _authState.value = AuthState.Initial
                }
                else -> {
                    _authState.value = AuthState.Error("Error al obtener credenciales: ${e.message}")
                }
            }
        }
    }
    
    // Manejador para la respuesta de credenciales
    private fun handleCredentialResponse(response: GetCredentialResponse) {
        Log.d(TAG, "Procesando respuesta de credencial")
        when (val credential = response.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        Log.d(TAG, "Credencial de tipo Google ID Token")
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        handleGoogleIdToken(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Error al procesar la credencial de Google: ${e.message}", e)
                        _authState.value = AuthState.Error("Error al procesar la credencial de Google: ${e.message}")
                    }
                } else {
                    Log.e(TAG, "Tipo de credencial no soportado: ${credential.type}")
                    _authState.value = AuthState.Error("Tipo de credencial no soportado: ${credential.type}")
                }
            }
            else -> {
                Log.e(TAG, "Credencial no soportada: ${credential::class.java.simpleName}")
                _authState.value = AuthState.Error("Credencial no soportada")
            }
        }
    }
    
    // Método para autenticar con Firebase usando el ID token
    private fun handleGoogleIdToken(idToken: String) {
        Log.d(TAG, "Iniciando autenticación con Firebase usando ID token de Google")
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
                Log.d(TAG, "Autenticación con Google exitosa")
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                Log.e(TAG, "Error al autenticar con Firebase: ${e.message}", e)
                _authState.value = AuthState.Error("Error al autenticar con Firebase: ${e.message}")
            }
        }
    }
    
    // Métodos de validación
    private fun isValidEmail(email: String): Boolean {
        val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        Log.d(TAG, "Validación de email: $email es ${if (isValid) "válido" else "inválido"}")
        return isValid
    }
    
    private fun isValidPassword(password: String): Boolean {
        val isValid = password.length >= 6
        Log.d(TAG, "Validación de contraseña: ${if (isValid) "válida" else "inválida"}")
        return isValid
    }
    
    // Método para convertir errores de Firebase en mensajes amigables
    private fun parseFirebaseAuthError(errorMessage: String): String {
        val parsedMessage = when {
            errorMessage.contains("email address is badly formatted") -> 
                "El formato del correo electrónico no es válido"
            errorMessage.contains("password is invalid") -> 
                "La contraseña es incorrecta"
            errorMessage.contains("no user record") -> 
                "No existe una cuenta con este correo electrónico"
            errorMessage.contains("email address is already in use") -> 
                "Este correo electrónico ya está registrado"
            errorMessage.contains("network error") -> 
                "Error de conexión. Comprueba tu conexión a Internet"
            errorMessage.contains("INVALID_LOGIN_CREDENTIALS") ->
                "Correo electrónico o contraseña incorrectos"
            else -> errorMessage
        }
        Log.d(TAG, "Error original: '$errorMessage', mensaje traducido: '$parsedMessage'")
        return parsedMessage
    }
}

sealed class AuthState {
    data object Initial : AuthState()
    data object Loading : AuthState()
    data object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class PasswordResetState {
    data object Initial : PasswordResetState()
    data object Loading : PasswordResetState()
    data object Success : PasswordResetState()
    data class Error(val message: String) : PasswordResetState()
}

sealed class ValidationState {
    data object Valid : ValidationState()
    data object InvalidEmail : ValidationState()
    data object InvalidPassword : ValidationState()
    data object PasswordsDoNotMatch : ValidationState()
} 