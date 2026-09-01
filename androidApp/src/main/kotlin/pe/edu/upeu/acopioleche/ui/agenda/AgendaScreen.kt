package pe.edu.upeu.acopioleche.ui.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upeu.acopioleche.domain.model.Reunion
import pe.edu.upeu.acopioleche.domain.model.TipoEvento
import pe.edu.upeu.acopioleche.ui.common.AcopioIcons
import pe.edu.upeu.acopioleche.ui.common.BotonGrande
import pe.edu.upeu.acopioleche.ui.common.FormInput
import pe.edu.upeu.acopioleche.ui.common.NavHeader
import pe.edu.upeu.acopioleche.ui.common.VarianteBoton
import pe.edu.upeu.acopioleche.ui.common.minutosDelDiaAHora
import pe.edu.upeu.acopioleche.ui.theme.AzulSecundario
import pe.edu.upeu.acopioleche.ui.theme.AzulTextoBoton
import pe.edu.upeu.acopioleche.ui.theme.BordeSuave
import pe.edu.upeu.acopioleche.ui.theme.Dorado
import pe.edu.upeu.acopioleche.ui.theme.FondoSecundario
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeExitoOscuro
import pe.edu.upeu.acopioleche.ui.theme.VerdeExitoTexto

@Composable
fun AgendaScreen(
    onVolver: () -> Unit,
    onIrAAsistencia: (Reunion) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AgendaViewModel = viewModel(),
) {
    val estado by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            NavHeader(
                titulo = "Agenda",
                onVolver = onVolver,
                contenidoDerecha = {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Dorado, RoundedCornerShape(11.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        IconButton(onClick = { viewModel.mostrarFormularioNuevo(true) }) {
                            Icon(AcopioIcons.Mas, contentDescription = "Nuevo evento", tint = AzulTextoBoton)
                        }
                    }
                },
            )

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text("Septiembre 2026", style = MaterialTheme.typography.titleSmall, color = AzulTextoBoton)
                }
                items(estado.eventos) { evento ->
                    TarjetaEvento(
                        evento = evento,
                        convocados = estado.convocadosPorEvento[evento.id] ?: 0,
                        onControlAsistencia = { onIrAAsistencia(evento) },
                    )
                }
            }
        }

        if (estado.mostrarFormularioNuevo) {
            HojaNuevoEvento(estado = estado, viewModel = viewModel)
        }
    }
}

@Composable
private fun TarjetaEvento(evento: Reunion, convocados: Int, onControlAsistencia: () -> Unit) {
    val esCapacitacion = evento.tipo == TipoEvento.CAPACITACION
    val colorTipo = if (esCapacitacion) AzulSecundario else VerdeExitoOscuro
    val fondoTipo = if (esCapacitacion) pe.edu.upeu.acopioleche.ui.theme.AzulFondoTarjeta else pe.edu.upeu.acopioleche.ui.theme.VerdeExitoFondo

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(fondoTipo, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Box(modifier = Modifier.size(8.dp).background(colorTipo, androidx.compose.foundation.shape.CircleShape))
            Text(
                if (esCapacitacion) "CAPACITACIÓN" else "REUNIÓN",
                style = MaterialTheme.typography.labelSmall,
                color = if (esCapacitacion) AzulSecundario else VerdeExitoTexto,
            )
        }
        Column(modifier = Modifier.padding(14.dp)) {
            Text(evento.tema, style = MaterialTheme.typography.titleSmall, color = AzulTextoBoton)
            Row(modifier = Modifier.padding(top = 9.dp, bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Icon(AcopioIcons.Calendario, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(13.dp))
                    Text(evento.fecha, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Icon(AcopioIcons.Reloj, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(13.dp))
                    Text(
                        "${minutosDelDiaAHora(evento.horaInicioMinutos)} – ${minutosDelDiaAHora(evento.horaFinMinutos)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "$convocados convocados",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextoSecundario,
                    modifier = Modifier.background(FondoSecundario, RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 4.dp),
                )
                Text(
                    "Control asistencia",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier
                        .background(AzulTextoBoton, RoundedCornerShape(10.dp))
                        .clickable(onClick = onControlAsistencia)
                        .padding(horizontal = 13.dp, vertical = 9.dp),
                )
            }
        }
    }
}

@Composable
private fun HojaNuevoEvento(estado: AgendaUiState, viewModel: AgendaViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulTextoBoton.copy(alpha = 0.65f))
            .clickable(onClick = { viewModel.mostrarFormularioNuevo(false) }),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .clickable(enabled = false) {}
                .padding(top = 22.dp, start = 20.dp, end = 20.dp, bottom = 28.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Nuevo evento", style = MaterialTheme.typography.headlineSmall, color = AzulTextoBoton)
                Icon(
                    AcopioIcons.Cerrar,
                    contentDescription = "Cerrar",
                    tint = TextoSecundario,
                    modifier = Modifier.size(22.dp).clickable { viewModel.mostrarFormularioNuevo(false) },
                )
            }
            FormInput("Tema del evento *", "Ej. Capacitación de higiene", estado.nuevoTema, viewModel::cambiarTema)
            FormInput("Fecha", "Ej. 20 Sep 2026", estado.nuevaFecha, viewModel::cambiarFecha)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    FormInput("Hora inicio", "09:00", estado.nuevaHoraInicio, viewModel::cambiarHoraInicio)
                }
                Box(modifier = Modifier.weight(1f)) {
                    FormInput("Hora fin", "11:00", estado.nuevaHoraFin, viewModel::cambiarHoraFin)
                }
            }
            BotonGrande(
                etiqueta = "Crear evento",
                onClick = viewModel::crearEvento,
                variante = VarianteBoton.DORADO,
                habilitado = estado.puedeCrearEvento,
            )
        }
    }
}
