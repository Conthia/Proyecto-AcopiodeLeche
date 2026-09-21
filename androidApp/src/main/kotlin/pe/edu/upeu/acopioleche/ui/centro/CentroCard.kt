package pe.edu.upeu.acopioleche.ui.centro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.presentation.centro.CentroConEstadisticas
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.EstadoBadge
import pe.edu.upeu.acopioleche.ui.components.StatTile
import pe.edu.upeu.acopioleche.ui.theme.AmbarFondo
import pe.edu.upeu.acopioleche.ui.theme.AmbarTexto
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.RojoFondo
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro
import pe.edu.upeu.acopioleche.ui.theme.VerdeSuaveFondo

@Composable
fun CentroCard(
    centro: CentroConEstadisticas,
    modifier: Modifier = Modifier,
    alToggleActivo: (() -> Unit)? = null,
    alEditar: (() -> Unit)? = null,
    alEliminar: (() -> Unit)? = null,
) {
    AppCard(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = centro.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${centro.ubicacion} · tanque ${centro.capacidadTanqueLitros.toInt()} L",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                )
            }
            if (!centro.activo) {
                EstadoBadge(texto = "Inactivo", color = RojoAlerta, colorFondo = RojoFondo)
            } else if (centro.operativo) {
                EstadoBadge(texto = "Operativo", color = VerdeOscuro, colorFondo = VerdeSuaveFondo)
            } else {
                EstadoBadge(texto = "Sin señal", color = AmbarTexto, colorFondo = AmbarFondo)
            }
        }
        HorizontalDivider()
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatTile(valor = "${centro.numeroProveedores}", etiqueta = "proveedores", modifier = Modifier.weight(1f))
            StatTile(valor = "${centro.litrosHoy}", etiqueta = "litros/día", modifier = Modifier.weight(1f))
            StatTile(valor = "${centro.numeroAcopiadores}", etiqueta = "acopiadores", modifier = Modifier.weight(1f))
        }

        if (alToggleActivo != null || alEditar != null || alEliminar != null) {
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (alToggleActivo != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (centro.activo) "Activo" else "Inactivo",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario,
                        )
                        Switch(checked = centro.activo, onCheckedChange = { alToggleActivo() })
                    }
                }
                Row {
                    if (alEditar != null) {
                        IconButton(onClick = alEditar) {
                            Icon(imageVector = Icons.Filled.Edit, contentDescription = "Editar", tint = VerdeOscuro)
                        }
                    }
                    if (alEliminar != null) {
                        IconButton(onClick = alEliminar) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Eliminar", tint = RojoAlerta)
                        }
                    }
                }
            }
        }
    }
}
