package pe.edu.upeu.acopioleche.ui.dashboard.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.domain.model.EquipoCampo
import pe.edu.upeu.acopioleche.domain.model.EstadoConexion
import pe.edu.upeu.acopioleche.ui.theme.AmbarPendiente
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

@Composable
fun EquipoCampoRow(equipo: EquipoCampo, modifier: Modifier = Modifier) {
    val (color, texto) = when (val estado = equipo.estadoConexion) {
        is EstadoConexion.AlDia -> VerdeOscuro to "al día"
        is EstadoConexion.Pendiente -> AmbarPendiente to "${estado.diasSinSincronizar} día(s) pendiente"
        is EstadoConexion.SinConexion -> RojoAlerta to "sin conexión ${estado.dias} d"
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.size(8.dp).background(color = color, shape = CircleShape),
        )
        Text(text = equipo.nombre, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(text = texto, style = MaterialTheme.typography.labelSmall, color = color)
    }
}
