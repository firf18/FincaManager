package com.example.fincamanager.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

// Esquema de colores monocromático oscuro para la autenticación
private val DarkAuthColorScheme =
        darkColorScheme(
                primary = ButtonActive,
                onPrimary = TextPrimary,
                primaryContainer = ButtonActive,
                onPrimaryContainer = TextPrimary,
                secondary = TextSecondary,
                onSecondary = TextPrimary,
                secondaryContainer = DarkSurface,
                onSecondaryContainer = TextSecondary,
                background = DarkBackground,
                onBackground = TextPrimary,
                surface = DarkSurface,
                onSurface = TextPrimary,
                surfaceVariant = DarkSurface.copy(alpha = 0.7f),
                onSurfaceVariant = TextSecondary,
                error = ErrorRed,
                onError = TextPrimary
        )

// Esquema de colores original para el resto de la aplicación
private val DarkColorScheme =
        darkColorScheme(
                primary = PrimaryGreen,
                onPrimary = Color.White,
                primaryContainer = PrimaryGreen.copy(alpha = 0.8f),
                onPrimaryContainer = Color.White,
                secondary = SecondaryGreen,
                onSecondary = Color.Black,
                secondaryContainer = SecondaryGreen.copy(alpha = 0.6f),
                onSecondaryContainer = Color.Black,
                tertiary = EarthBrown,
                onTertiary = Color.White,
                tertiaryContainer = WarmTaupe,
                onTertiaryContainer = Color.Black,
                background = Color(0xFF1C1C1C),
                onBackground = SandyBeige,
                surface = Color(0xFF282828),
                onSurface = SandyBeige,
                error = ErrorRed,
                onError = Color.White,
                surfaceVariant = Color(0xFF3A3A3A),
                onSurfaceVariant = SoftBrown
        )

private val LightColorScheme =
        lightColorScheme(
                primary = PastelBlue,
                onPrimary = DarkPastelText,
                primaryContainer = PastelMint,
                onPrimaryContainer = DarkPastelText,
                secondary = PastelPink,
                onSecondary = DarkPastelText,
                secondaryContainer = PastelLavender,
                onSecondaryContainer = DarkPastelText,
                tertiary = PastelYellow,
                onTertiary = DarkPastelText,
                tertiaryContainer = PastelYellow.copy(alpha = 0.4f),
                onTertiaryContainer = DarkPastelText,
                background = VeryLightGray,
                onBackground = DarkPastelText,
                surface = Color.White,
                onSurface = DarkPastelText,
                error = ErrorRed,
                onError = Color.White,
                surfaceVariant = PastelLavender.copy(alpha = 0.3f),
                onSurfaceVariant = DarkPastelText
        )

// Esquema de colores claro para la autenticación
private val LightAuthColorScheme =
        lightColorScheme(
                primary = PrimaryGreen,
                onPrimary = Color.White,
                primaryContainer = PrimaryGreen.copy(alpha = 0.8f),
                onPrimaryContainer = Color.White,
                secondary = SecondaryGreen,
                onSecondary = TextDark,
                secondaryContainer = SecondaryGreen.copy(alpha = 0.6f),
                onSecondaryContainer = TextDark,
                background = LightBackground,
                onBackground = TextDark,
                surface = TileBackgroundLight,
                onSurface = TextDark,
                surfaceVariant = Color.White,
                onSurfaceVariant = TextMuted,
                error = ErrorRed,
                onError = Color.White
        )

@Composable
fun FincaManagerTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        // Dynamic color is available on Android 12+
        dynamicColor: Boolean = false,
        // Para las pantallas de autenticación usaremos el tema oscuro monocromático
        isAuthScreen: Boolean = false,
        useLightAuth: Boolean = true,
        content: @Composable () -> Unit
) {
    val colorScheme =
            when {
                dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val context = LocalContext.current
                    if (darkTheme) dynamicDarkColorScheme(context)
                    else dynamicLightColorScheme(context)
                }
                // Para las pantallas de autenticación, seleccionamos entre light y dark auth
                isAuthScreen && useLightAuth -> LightAuthColorScheme
                isAuthScreen -> DarkAuthColorScheme
                // Para el resto de la app, respetamos el tema del sistema
                darkTheme -> DarkColorScheme
                else -> LightColorScheme
            }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            // Enfoque moderno: Configuramos la vista para ser edge-to-edge
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // Controlamos la apariencia de las barras del sistema usando WindowInsetsController
            val controller = WindowCompat.getInsetsController(window, view)
            
            // Configuramos la visibilidad y comportamiento de las barras del sistema
            controller.systemBarsBehavior = 
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            
            if (isAuthScreen) {
                // Para pantallas de autenticación, usar tema oscuro para barras del sistema
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            } else {
                // Para el resto de la app, la apariencia de las barras se basa en el tema
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
            
            // Establecemos colores de fondo scrim para barras del sistema
            // Usamos colorScheme.background o colores específicos según el diseño
            val backgroundColor = if (isAuthScreen) {
                if (useLightAuth) LightBackground else DarkBackground
            } else {
                colorScheme.background
            }
            
            // Nota: aunque estas propiedades tienen warnings de deprecación,
            // son necesarias para tener un color de fondo para las barras del sistema
            // en implementaciones de edge-to-edge
            window.setBackgroundDrawableResource(android.R.color.transparent)
            @Suppress("DEPRECATION")
            window.statusBarColor = backgroundColor.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = backgroundColor.toArgb()
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
