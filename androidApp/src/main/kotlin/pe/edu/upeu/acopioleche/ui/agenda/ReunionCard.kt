package pe.edu.upeu.acopioleche.ui.agenda

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import pe.edu.upeu.acopioleche.domain.model.TipoEvento
import pe.edu.upeu.acopioleche.presentation.agenda.ReunionResumen
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.EstadoBadge
import pe.edu.upeu.acopioleche.ui.theme.AmbarTexto
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro
import pe.edu.upeu.acopioleche.ui.theme.VerdeSuaveFondo

@Composable
fun ReunionCard(
    reunion: ReunionResumen,
    alTocar: () -> Unit,
    modifier: Modifier = Modifier,
    alEditar: (() -> Unit)? = null,
    alEliminar: (() -> Unit)? = null,
) {
    AppCard(
        modifier = modifier.fillMaxWidth().clickable(onClick = alTocar),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            val etiquetaTipo = if (reunion.tipo == TipoEvento.CAPACITACION) "Capacitación" else "Reunión"
            EstadoBadge(texto = etiquetaTipo, color = VerdeOscuro, colorFondo = VerdeSuaveFondo)
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
        Text(text = reunion.tema, style = MaterialTheme.typography.titleMedium)
        Text(
            text = "${reunion.fecha} · ${formatearRangoHoras(reunion.horaInicioMinutos, reunion.horaFinMinutos)} · ${reunion.lugar}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario,
        )
        HorizontalDivider()
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            if (reunion.yaOcurrio) {
                Text(
                    text = "Asistencia ${reunion.numeroAsistentes}/${reunion.numeroConvocados}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = VerdeOscuro,
                )
                Text(text = "Ver acta ›", style = MaterialTheme.typography.bodyLarge, color = VerdeOscuro)
            } else {
                Text(
                    text = "${reunion.numeroConvocados} convocados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AmbarTexto,
                )
                Text(text = "Tomar asistencia ›", style = MaterialTheme.typography.bodyLarge, color = VerdeOscuro)
            }
        }
    }
}
