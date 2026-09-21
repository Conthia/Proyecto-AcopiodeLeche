package pe.edu.upeu.acopioleche.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.domain.model.EstadoEntrega
import pe.edu.upeu.acopioleche.domain.model.Turno
import pe.edu.upeu.acopioleche.presentation.core.EntregaResumen
import pe.edu.upeu.acopioleche.ui.theme.AmbarFondo
import pe.edu.upeu.acopioleche.ui.theme.AmbarPendiente
import pe.edu.upeu.acopioleche.ui.theme.AmbarTexto
import pe.edu.upeu.acopioleche.ui.theme.BordeTarjeta
import pe.edu.upeu.acopioleche.ui.theme.FondoTarjeta
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.RojoFondo
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

@Composable
fun EntregaRow(
    entrega: EntregaResumen,
    modifier: Modifier = Modifier,
    mostrarAvatar: Boolean = true,
    alEditar: (() -> Unit)? = null,
    alCancelar: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        border = BorderStroke(width = 1.dp, color = BordeTarjeta),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (mostrarAvatar) {
                AvatarIniciales(nombre = entrega.nombreProveedor)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = entrega.nombreProveedor, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${entrega.codigoProveedor} · ${entrega.cantidadPorongos} porongo(s) · ${etiquetaTurno(entrega.turno)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                )
                RenderEstadoBadge(entrega.estado)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "${entrega.volumenLitros} L", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    text = if (entrega.sincronizada) "enviado" else "en cola",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (entrega.sincronizada) VerdeOscuro else AmbarPendiente,
                )
                entrega.diferenciaLitros?.let { diferencia ->
                    if (diferencia != 0.0) {
                        Text(
                            text = "planta: ${entrega.volumenPlantaLitros} L (${if (diferencia > 0) "+" else ""}$diferencia)",
                            style = MaterialTheme.typography.labelSmall,
                            color = RojoAlerta,
                        )
                    }
                }
                if (entrega.estado is EstadoEntrega.Pendiente || entrega.estado is EstadoEntrega.NoRecogida) {
                    Row {
                        if (alEditar != null) {
                            IconButton(onClick = alEditar) {
                                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Editar entrega", tint = VerdeOscuro)
                            }
                        }
                        if (alCancelar != null) {
                            IconButton(onClick = alCancelar) {
                                Icon(imageVector = Icons.Filled.Cancel, contentDescription = "Cancelar entrega", tint = RojoAlerta)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderEstadoBadge(estado: EstadoEntrega) {
    when (estado) {
        is EstadoEntrega.NoRecogida -> EstadoBadge(texto = "No recogido (${estado.motivo})", color = AmbarTexto, colorFondo = AmbarFondo)
        is EstadoEntrega.Rechazada -> EstadoBadge(texto = "Rechazada", color = RojoAlerta, colorFondo = RojoFondo)
        is EstadoEntrega.Cancelada -> EstadoBadge(texto = "Cancelada", color = RojoAlerta, colorFondo = RojoFondo)
        else -> Unit
    }
}

private fun etiquetaTurno(turno: Turno): String =
    when (turno) {
        Turno.MANANA -> "Turno mañana"
        Turno.TARDE -> "Turno tarde"
    }
