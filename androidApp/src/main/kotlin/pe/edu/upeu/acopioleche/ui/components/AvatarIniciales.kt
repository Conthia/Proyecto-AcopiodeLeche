package pe.edu.upeu.acopioleche.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro
import pe.edu.upeu.acopioleche.ui.theme.VerdeSuaveFondo

@Composable
fun AvatarIniciales(nombre: String, modifier: Modifier = Modifier) {
    val iniciales = nombre.split(" ")
        .filter { it.length > 2 }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString(separator = "")

    Box(
        modifier = modifier
            .size(44.dp)
            .background(color = VerdeSuaveFondo, shape = RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = iniciales, color = VerdeOscuro, style = MaterialTheme.typography.labelSmall)
    }
}
