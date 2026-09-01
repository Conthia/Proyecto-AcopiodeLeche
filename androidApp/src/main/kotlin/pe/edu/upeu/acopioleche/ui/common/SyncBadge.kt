package pe.edu.upeu.acopioleche.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.ui.theme.AmarilloAlerta
import pe.edu.upeu.acopioleche.ui.theme.VerdeExito

@Composable
fun SyncBadge(estado: EstadoSincronizacion, modifier: Modifier = Modifier) {
    val sincronizado = estado == EstadoSincronizacion.SINCRONIZADO
    val color = if (sincronizado) VerdeExito else AmarilloAlerta
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = if (sincronizado) AcopioIcons.Check else AcopioIcons.Sincronizando,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp),
        )
        Text(
            text = if (sincronizado) "Sincronizado" else "Pendiente",
            style = MaterialTheme.typography.labelSmall,
            color = color,
        )
    }
}
