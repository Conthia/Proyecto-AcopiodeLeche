package pe.edu.upeu.acopioleche.ui.asistencia

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import pe.edu.upeu.acopioleche.domain.model.Reunion
import pe.edu.upeu.acopioleche.domain.model.TipoActor
import pe.edu.upeu.acopioleche.ui.common.AcopioIcons
import pe.edu.upeu.acopioleche.ui.common.BotonGrande
import pe.edu.upeu.acopioleche.ui.common.NavHeader
import pe.edu.upeu.acopioleche.ui.common.VarianteBoton
import pe.edu.upeu.acopioleche.ui.common.minutosDelDiaAHora
import pe.edu.upeu.acopioleche.ui.theme.AzulTextoBoton
import pe.edu.upeu.acopioleche.ui.theme.BordeClaro
import pe.edu.upeu.acopioleche.ui.theme.BordeSuave
import pe.edu.upeu.acopioleche.ui.theme.FondoSecundario
import pe.edu.upeu.acopioleche.ui.theme.RojoErrorFondo
import pe.edu.upeu.acopioleche.ui.theme.RojoErrorTexto
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.TextoTenue
import pe.edu.upeu.acopioleche.ui.theme.VerdeExito
import pe.edu.upeu.acopioleche.ui.theme.VerdeExitoFondo
import pe.edu.upeu.acopioleche.ui.theme.VerdeExitoTexto

@Composable
fun ControlAsistenciaScreen(
    reunion: Reunion,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ControlAsistenciaViewModel = viewModel(
        factory = viewModelFactory { initializer { ControlAsistenciaViewModel(reunion) } },
    ),
) {
    val estado by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        NavHeader(titulo = "Control de asistencia", onVolver = onVolver)

        Column(modifier = Modifier.fillMaxWidth().background(FondoSecundario).padding(horizontal = 16.dp, vertical = 11.dp)) {
            Text(estado.reunion.tema, style = MaterialTheme.typography.titleSmall, color = AzulTextoBoton)
            Text(
                "${estado.reunion.fecha} · ${minutosDelDiaAHora(estado.reunion.horaInicioMinutos)} – ${minutosDelDiaAHora(estado.reunion.horaFinMinutos)}",
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundario,
                modifier = Modifier.padding(top = 3.dp),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.background(VerdeExitoFondo, RoundedCornerShape(9.dp)).padding(horizontal = 12.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text("${estado.totalPresentes}", style = MaterialTheme.typography.titleMedium, color = VerdeExitoTexto)
                Text("presentes", style = MaterialTheme.typography.labelSmall, color = VerdeExitoTexto)
            }
            Row(
                modifier = Modifier.background(RojoErrorFondo, RoundedCornerShape(9.dp)).padding(horizontal = 12.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text("${estado.totalAusentes}", style = MaterialTheme.typography.titleMedium, color = RojoErrorTexto)
                Text("ausentes", style = MaterialTheme.typography.labelSmall, color = RojoErrorTexto)
            }
            Column(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(BordeClaro, RoundedCornerShape(4.dp))) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(estado.porcentajePresentes / 100f)
                            .height(8.dp)
                            .background(VerdeExito, RoundedCornerShape(4.dp)),
                    )
                }
                Text(
                    "${estado.porcentajePresentes}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextoTenue,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
                )
            }
        }

        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
            items(estado.asistentes) { asistente ->
                val presente = estado.presentes[asistente.id] == true
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (presente) VerdeExitoFondo else Color.White)
                        .clickable { viewModel.alternarPresente(asistente.id) }
                        .padding(horizontal = 16.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier.size(42.dp).background(if (presente) VerdeExito else FondoSecundario, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (presente) {
                            Icon(AcopioIcons.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        } else {
                            Text(asistente.nombre.first().toString(), style = MaterialTheme.typography.titleMedium, color = TextoSecundario)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(asistente.nombre, style = MaterialTheme.typography.titleMedium, color = AzulTextoBoton)
                        Text(
                            if (asistente.tipoActor == TipoActor.PROVEEDOR) "Proveedor" else "Acopiador",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextoTenue,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(if (presente) VerdeExito else Color.Transparent, RoundedCornerShape(8.dp))
                            .border(2.dp, if (presente) VerdeExito else BordeClaro, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (presente) {
                            Icon(AcopioIcons.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
            BotonGrande(
                etiqueta = "Guardar asistencia (${estado.totalPresentes}/${estado.asistentes.size})",
                onClick = {
                    viewModel.construirRegistrosDeAsistencia()
                    onVolver()
                },
                variante = VarianteBoton.DORADO,
            )
        }
    }
}
