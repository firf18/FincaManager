// Mejorado por Freddy-Assistant: LoginScreen profesional, limpio y claro
package com.example.fincamanager.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fincamanager.R
import com.example.fincamanager.navigation.Routes
import com.example.fincamanager.ui.theme.*
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            if (!user.isAnonymous &&
                            (user.isEmailVerified ||
                                    user.providerData.any { it.providerId == "google.com" })
            ) {
                navController.navigate(Routes.Home.route) {
                    popUpTo(Routes.Login.route) { inclusive = true }
                }
            } else if (user.isAnonymous) authViewModel.signOut()
        }
    }

    // Usando el tema claro para la autenticación
    FincaManagerTheme(isAuthScreen = true, useLightAuth = true) {
        Box(modifier = Modifier.fillMaxSize().background(LightBackground)) {
            Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Título principal con estilo moderno
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
                        text = "Iniciar Sesión",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark,
                        modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campo de correo electrónico con estilo elevado
                OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = {
                            Icon(
                                    Icons.Default.Email,
                                    contentDescription = null,
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
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                ),
                        keyboardActions =
                                KeyboardActions(
                                        onNext = {
                                            focusManager.moveFocus(
                                                    androidx.compose.ui.focus.FocusDirection.Down
                                            )
                                        }
                                )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de contraseña con estilo elevado
                OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryGreen)
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
                                        contentDescription = null,
                                        tint = TextMuted
                                )
                            }
                        },
                        visualTransformation =
                                if (showPassword) VisualTransformation.None
                                else PasswordVisualTransformation(),
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
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done
                                ),
                        keyboardActions =
                                KeyboardActions(
                                        onDone = {
                                            focusManager.clearFocus()
                                            if (email.isNotBlank() && password.isNotBlank()) {
                                                authViewModel.signIn(email, password)
                                            }
                                        }
                                )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Enlace para olvidó contraseña
                TextButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                            "¿Olvidaste tu contraseña?",
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón principal de inicio de sesión
                Button(
                        onClick = { authViewModel.signIn(email, password) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = PrimaryGreen,
                                        contentColor = Color.White
                                ),
                        enabled = authState != AuthState.Loading
                ) {
                    if (authState == AuthState.Loading) {
                        CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                        )
                    } else {
                        Text("Iniciar sesión", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Divisor con texto "O"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray,
                        thickness = 1.dp
                    )
                    Text(
                        text = "O",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray,
                        thickness = 1.dp
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Botón de inicio de sesión con Google
                OutlinedButton(
                    onClick = { authViewModel.signInWithGoogle(context) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextDark
                    ),
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Iniciar sesión con Google",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Sección de registro
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "¿No tienes una cuenta?", color = TextMuted)
                    TextButton(onClick = { navController.navigate(Routes.Registration.route) }) {
                        Text("Regístrate", color = PrimaryGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Diálogo para restablecer contraseña
            if (showResetDialog) {
                AlertDialog(
                        onDismissRequest = { showResetDialog = false },
                        title = { Text("Restablecer contraseña") },
                        text = {
                            Column {
                                Text(
                                        "Ingresa tu correo electrónico y te enviaremos un enlace para restablecer tu contraseña.",
                                        color = TextDark
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedTextField(
                                        value = email,
                                        onValueChange = { email = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        label = { Text("Correo electrónico") },
                                        keyboardOptions =
                                                KeyboardOptions(keyboardType = KeyboardType.Email),
                                        colors =
                                                OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = PrimaryGreen,
                                                        focusedLabelColor = PrimaryGreen,
                                                        cursorColor = PrimaryGreen
                                                )
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                    onClick = {
                                        if (email.isNotBlank()) {
                                            authViewModel.resetPassword(email)
                                            showResetDialog = false
                                        }
                                    }
                            ) {
                                Text("Enviar", color = PrimaryGreen, fontWeight = FontWeight.Medium)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showResetDialog = false }) {
                                Text("Cancelar", color = TextMuted)
                            }
                        }
                )
            }
        }
    }
}
