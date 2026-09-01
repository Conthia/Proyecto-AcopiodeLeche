package pe.edu.upeu.acopioleche.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaClaro = lightColorScheme(
    primary = AzulTextoBoton,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = Dorado,
    onSecondary = AzulTextoBoton,
    background = FondoGeneral,
    onBackground = AzulTextoBoton,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = AzulTextoBoton,
    error = RojoError,
    onError = androidx.compose.ui.graphics.Color.White,
    outline = BordeClaro,
)

private val EsquemaOscuro = darkColorScheme(
    primary = Dorado,
    onPrimary = AzulTextoBoton,
    secondary = AzulSecundario,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    background = AzulMarinoOscuro,
    onBackground = androidx.compose.ui.graphics.Color.White,
    surface = AzulTextoBoton,
    onSurface = androidx.compose.ui.graphics.Color.White,
    error = RojoError,
    onError = androidx.compose.ui.graphics.Color.White,
    outline = BordeClaro,
)

@Composable
fun AcopioLecheTheme(
    usarTemaOscuro: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (usarTemaOscuro) EsquemaOscuro else EsquemaClaro
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AcopioTypography,
        content = content,
    )
}
