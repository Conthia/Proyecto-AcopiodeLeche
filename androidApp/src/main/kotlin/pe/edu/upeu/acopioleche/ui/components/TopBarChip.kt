package pe.edu.upeu.acopioleche.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Insignia pequeña usada como accesorio del [AppTopBar], p. ej. "3 local" o "En línea". */
@Composable
fun TopBarChip(texto: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(color = Color.White.copy(alpha = 0.16f), shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(text = texto, color = Color.White, style = MaterialTheme.typography.labelSmall)
    }
}
