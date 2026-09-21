package pe.edu.upeu.acopioleche.ui.proveedor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.presentation.proveedor.ProveedorConEntregas
import pe.edu.upeu.acopioleche.ui.components.AvatarIniciales
import pe.edu.upeu.acopioleche.ui.components.EstadoBadge
import pe.edu.upeu.acopioleche.ui.theme.AmbarFondo
import pe.edu.upeu.acopioleche.ui.theme.AmbarTexto
import pe.edu.upeu.acopioleche.ui.theme.BordeTarjeta
import pe.edu.upeu.acopioleche.ui.theme.FondoTarjeta
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.RojoFondo
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

@Composable
fun ProveedorRow(
    proveedor: ProveedorConEntregas,
    modifier: Modifier = Modifier,
    alEditar: (() -> Unit)? = null,
    alEliminar: (() -> Unit)? = null,
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        border = BorderStroke(width = 1.dp, color = BordeTarjeta),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarIniciales(nombre = proveedor.nombre)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = proveedor.nombre, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "${proveedor.sector} · ${proveedor.numeroVacas} vacas · ${proveedor.litrosHoy} L hoy",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                CalificacionBadge(calificacion = proveedor.calificacion)
                if (!proveedor.activo) {
                    EstadoBadge(
                        texto = "Retirado",
                        color = RojoAlerta,
                        colorFondo = RojoFondo,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
                if (proveedor.pendienteSync) {
                    EstadoBadge(
                        texto = "Pendiente de envío",
                        color = AmbarTexto,
                        colorFondo = AmbarFondo,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
            if (alEditar != null || alEliminar != null) {
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
