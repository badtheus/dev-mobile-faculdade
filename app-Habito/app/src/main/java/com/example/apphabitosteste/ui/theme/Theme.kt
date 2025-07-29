package com.example.apphabitosteste.ui.theme

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

// Cores base do seu aplicativo
private val Blue500 = Color(0xFF1976D2) // Azul principal
private val Blue100 = Color(0xFFBBDEFB) // Azul claro para navegação, etc.
private val LightBlue50 = Color(0xFFE3F2FD) // Fundo muito claro
private val LightBlue200 = Color(0xFF90CAF9) // Azul para dias marcados no calendário

// Esquema de Cores para o MODO CLARO
private val LightColorScheme = lightColorScheme(
    primary = Blue500, // Usado para botões, títulos, elementos principais
    onPrimary = Color.White, // Cor do texto/ícone em cima de 'primary'
    secondary = Blue100, // Usado para barras de navegação, elementos secundários
    onSecondary = Color.Black, // Cor do texto/ícone em cima de 'secondary'
    background = LightBlue50, // Fundo geral da tela
    onBackground = Color.Black, // Cor do texto/ícone em cima de 'background'
    surface = Color.White, // Fundo de Cards, Dialogs, Superfícies
    onSurface = Color.Black, // Cor do texto/ícone em cima de 'surface'
    secondaryContainer = LightBlue200, // Usado para dias marcados no calendário (novo)
    onSecondaryContainer = Color.Black // Cor do texto em cima de 'secondaryContainer'
)

// Esquema de Cores para o MODO ESCURO
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9), // Um azul mais claro para destaque no escuro
    onPrimary = Color.Black,
    secondary = Color(0xFF42A5F5), // Azul um pouco mais escuro para elementos secundários
    onSecondary = Color.White,
    background = Color(0xFF121212), // Fundo quase preto
    onBackground = Color(0xFFE0E0E0), // Texto claro no fundo escuro
    surface = Color(0xFF1E1E1E), // Fundo de Cards, Dialogs no escuro
    onSurface = Color(0xFFE0E0E0), // Texto claro em superfícies escuras
    secondaryContainer = Color(0xFF004D40), // Um tom verde-azulado escuro para dias marcados no escuro
    onSecondaryContainer = Color.White
)

@Composable
fun AppHabitosTesteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Por padrão, segue o tema do sistema
    // `dynamicColor` é para Android 12+ e usa cores do papel de parede do usuário.
    // Vamos manter como false para manter as cores que definimos.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme // Se darkTheme for true, usa DarkColorScheme
        else -> LightColorScheme // Senão, usa LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb() // Cor da barra de status
            // Ícones da barra de status (brancos no tema escuro, escuros no tema claro)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Aplica o tema Material Design ao seu conteúdo
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assume que você tem um arquivo Typography.kt
        content = content
    )
}