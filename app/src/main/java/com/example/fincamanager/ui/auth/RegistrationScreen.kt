package com.example.fincamanager.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fincamanager.R
import com.example.fincamanager.navigation.Routes
import com.example.fincamanager.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    val authState = authViewModel.authState.collectAsStateWithLifecycle()
    val validationState = authViewModel.validationState.collectAsStateWithLifecycle()
    val currentUser = authViewModel.currentUser.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Manejar cambios en el estado de autenticación
    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AuthState.Success -> {
                // Solo navegar si la autenticación fue exitosa y tenemos un usuario
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null && !user.isAnonymous) {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Registration.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            else -> {}
        }
    }

    // Si el usuario ya está autenticado, ir a Home
    LaunchedEffect(currentUser.value) {
        // Solo navegar si tenemos un usuario autenticado válido
        currentUser.value?.let { user ->
            if (!user.isAnonymous &&
                            (user.isEmailVerified ||
                                    user.providerData.any { it.providerId == "google.com" })
            ) {
                navController.navigate(Routes.Home.route) {
                    popUpTo(Routes.Registration.route) { inclusive = true }
                    launchSingleTop = true
                }
            } else {
                // Si hay algún problema con la autenticación, mejor cerrar sesión
                if (user.isAnonymous) {
                    authViewModel.signOut()
                }
            }
        }
    }

    // Aplicar el tema claro elegante para las pantallas de autenticación
    FincaManagerTheme(isAuthScreen = true, useLightAuth = true) {
        Box(modifier = Modifier.fillMaxSize().background(LightBackground)) {
            Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título principal con estilo elegante
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    Text(
                            text = "FINCA",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                    )
                    Text(
                            text = "MANAGER",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                            text = "Gestión inteligente para tu finca",
                            fontSize = 16.sp,
                            color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                        text = "Registro",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark,
                        modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campo para nombre
                OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        leadingIcon = {
                            Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Icono de persona",
                                    tint = PrimaryGreen
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = TileBackgroundLight,
                                        unfocusedContainerColor = TileBackgroundLight,
                                        focusedBorderColor = PrimaryGreen,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedLabelColor = PrimaryGreen,
                                        unfocusedLabelColor = TextMuted,
                                        cursorColor = PrimaryGreen
                                ),
                        keyboardOptions =
                                KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next
                                ),
                        keyboardActions =
                                androidx.compose.foundation.text.KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Campo para email
                OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = {
                            Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Icono de email",
                                    tint = PrimaryGreen
                            )
                        },
                        isError = validationState.value is ValidationState.InvalidEmail,
                        supportingText = {
                            if (validationState.value is ValidationState.InvalidEmail) {
                                Text(
                                        text = stringResource(R.string.error_invalid_email),
                                        color = ErrorRed
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = TileBackgroundLight,
                                        unfocusedContainerColor = TileBackgroundLight,
                                        focusedBorderColor = PrimaryGreen,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedLabelColor = PrimaryGreen,
                                        unfocusedLabelColor = TextMuted,
                                        cursorColor = PrimaryGreen,
                                        errorBorderColor = ErrorRed,
                                        errorLabelColor = ErrorRed,
                                        errorSupportingTextColor = ErrorRed
                                ),
                        keyboardOptions =
                                KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                ),
                        keyboardActions =
                                androidx.compose.foundation.text.KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Campo para contraseña
                OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Icono de contraseña",
                                    tint = PrimaryGreen
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                        painter =
                                                painterResource(
                                                        id =
                                                                if (showPassword)
                                                                        R.drawable.ic_visibility
                                                                else R.drawable.ic_visibility_off
                                                ),
                                        contentDescription =
                                                if (showPassword) "Ocultar contraseña"
                                                else "Mostrar contraseña",
                                        tint = TextMuted
                                )
                            }
                        },
                        visualTransformation =
                                if (showPassword) VisualTransformation.None
                                else PasswordVisualTransformation(),
                        isError = validationState.value is ValidationState.InvalidPassword,
                        supportingText = {
                            if (validationState.value is ValidationState.InvalidPassword) {
                                Text(
                                        text = stringResource(R.string.error_invalid_password),
                                        color = ErrorRed
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = TileBackgroundLight,
                                        unfocusedContainerColor = TileBackgroundLight,
                                        focusedBorderColor = PrimaryGreen,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedLabelColor = PrimaryGreen,
                                        unfocusedLabelColor = TextMuted,
                                        cursorColor = PrimaryGreen,
                                        errorBorderColor = ErrorRed,
                                        errorLabelColor = ErrorRed,
                                        errorSupportingTextColor = ErrorRed
                                ),
                        keyboardOptions =
                                KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Next
                                ),
                        keyboardActions =
                                androidx.compose.foundation.text.KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Campo para confirmar contraseña
                OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña") },
                        leadingIcon = {
                            Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Icono de contraseña",
                                    tint = PrimaryGreen
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                        painter =
                                                painterResource(
                                                        id =
                                                                if (showConfirmPassword)
                                                                        R.drawable.ic_visibility
                                                                else R.drawable.ic_visibility_off
                                                ),
                                        contentDescription =
                                                if (showConfirmPassword) "Ocultar contraseña"
                                                else "Mostrar contraseña",
                                        tint = TextMuted
                                )
                            }
                        },
                        visualTransformation =
                                if (showConfirmPassword) VisualTransformation.None
                                else PasswordVisualTransformation(),
                        isError = validationState.value is ValidationState.PasswordsDoNotMatch,
                        supportingText = {
                            if (validationState.value is ValidationState.PasswordsDoNotMatch) {
                                Text(
                                        text = stringResource(R.string.error_passwords_dont_match),
                                        color = ErrorRed
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = TileBackgroundLight,
                                        unfocusedContainerColor = TileBackgroundLight,
                                        focusedBorderColor = PrimaryGreen,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedLabelColor = PrimaryGreen,
                                        unfocusedLabelColor = TextMuted,
                                        cursorColor = PrimaryGreen,
                                        errorBorderColor = ErrorRed,
                                        errorLabelColor = ErrorRed,
                                        errorSupportingTextColor = ErrorRed
                                ),
                        keyboardOptions =
                                KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done
                                ),
                        keyboardActions =
                                androidx.compose.foundation.text.KeyboardActions(
                                        onDone = {
                                            focusManager.clearFocus()
                                            if (email.isNotBlank() &&
                                                            password.isNotBlank() &&
                                                            confirmPassword.isNotBlank()
                                            ) {
                                                authViewModel.signUp(
                                                        email,
                                                        password,
                                                        confirmPassword
                                                )
                                            }
                                        }
                                )
                )

                // Mensaje de error
                AnimatedVisibility(visible = authState.value is AuthState.Error) {
                    Text(
                            text = (authState.value as? AuthState.Error)?.message ?: "",
                            color = ErrorRed,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón de registro
                Button(
                        onClick = { authViewModel.signUp(email, password, confirmPassword) },
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = PrimaryGreen,
                                        contentColor = Color.White
                                ),
                        shape = RoundedCornerShape(28.dp),
                        enabled = !authState.value.equals(AuthState.Loading)
                ) {
                    if (authState.value.equals(AuthState.Loading)) {
                        CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                        )
                    } else {
                        Text("Registrarse", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Separador "O regístrate con"
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = TextMuted.copy(alpha = 0.5f)
                    )
                    Text(
                            text = "O regístrate con",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = TextMuted.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Botón de registro con Google
                Button(
                        onClick = { authViewModel.signInWithGoogle(context) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = TileBackgroundLight,
                                        contentColor = TextDark
                                ),
                        shape = RoundedCornerShape(28.dp)
                ) {
                    Image(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Logo de Google",
                            modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrarse con Google", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Enlace para iniciar sesión
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("¿Ya tienes una cuenta?", color = TextMuted)
                    TextButton(
                            onClick = {
                                navController.navigate(Routes.Login.route) {
                                    popUpTo(Routes.Registration.route) { inclusive = true }
                                }
                            }
                    ) { Text("Iniciar sesión", color = PrimaryGreen, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}
