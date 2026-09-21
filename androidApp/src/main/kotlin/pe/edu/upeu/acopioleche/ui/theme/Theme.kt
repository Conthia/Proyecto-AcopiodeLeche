package pe.edu.upeu.acopioleche.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val AcopioLecheColorScheme = lightColorScheme(
    primary = VerdeOscuro,
    onPrimary = FondoTarjeta,
    secondary = AmbarPendiente,
    error = RojoAlerta,
    background = FondoPantalla,
    onBackground = TextoPrincipal,
    surface = FondoTarjeta,
    onSurface = TextoPrincipal,
    surfaceVariant = FondoTarjetaSutil,
    outline = BordeSutil,
)

private val AcopioLecheTypography = Typography(
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 15.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 11.sp, letterSpacing = 0.9.sp),
)

@Composable
fun AcopioLecheTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AcopioLecheColorScheme,
        typography = AcopioLecheTypography,
        content = content,
    )
}
