package pe.edu.upeu.acopioleche.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario

@Composable
fun FormLabel(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = TextoSecundario,
        modifier = modifier.padding(bottom = 7.dp),
    )
}
