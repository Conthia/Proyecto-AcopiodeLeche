package pe.edu.upeu.acopioleche.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.ui.theme.TextoTenue

@Composable
fun BotonCerrarSesion(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.Center) {
        TextButton(onClick = onClick) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(AcopioIcons.CerrarSesion, contentDescription = null, tint = TextoTenue, modifier = Modifier.size(14.dp))
                Text("Cerrar sesión", style = MaterialTheme.typography.bodySmall, color = TextoTenue)
            }
        }
    }
}
