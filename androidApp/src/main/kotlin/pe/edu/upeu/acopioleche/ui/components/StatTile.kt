package pe.edu.upeu.acopioleche.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pe.edu.upeu.acopioleche.ui.theme.TextoPrincipal
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario

@Composable
fun StatTile(valor: String, etiqueta: String, modifier: Modifier = Modifier, color: androidx.compose.ui.graphics.Color = TextoPrincipal) {
    Column(modifier = modifier) {
        Text(text = valor, style = MaterialTheme.typography.headlineSmall, color = color)
        Text(text = etiqueta, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
    }
}
