package pe.edu.upeu.acopioleche.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario

@Composable
fun SectionLabel(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto.uppercase(),
        color = TextoSecundario,
        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.padding(top = 4.dp, bottom = 2.dp),
    )
}
