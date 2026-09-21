package pe.edu.upeu.acopioleche.ui.agenda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.domain.model.Reunion
import pe.edu.upeu.acopioleche.domain.model.TipoEvento
import pe.edu.upeu.acopioleche.presentation.agenda.ReunionResumen
import pe.edu.upeu.acopioleche.presentation.agenda.ReunionesUiState
import pe.edu.upeu.acopioleche.presentation.agenda.ReunionesViewModel
import pe.edu.upeu.acopioleche.presentation.core.UiState
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.EstadoCargando
import pe.edu.upeu.acopioleche.ui.components.EstadoError
import pe.edu.upeu.acopioleche.ui.components.EstadoVacio
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

@Composable
fun ReunionesScreen(alAbrirAsistencia: (reunionId: String) -> Unit) {
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        ReunionesViewModel(
            scope = scope,
            reunionRepository = ServiceLocator.reunionRepository,
            asistenciaRepository = ServiceLocator.asistenciaRepository,
        )
    }
    val uiState by viewModel.uiState.collectAsState()
    val exito = uiState as? UiState.Exito<ReunionesUiState>

    var mostrarDialogoNuevo by remember { mutableStateOf(false) }
    var reunionAEditar by remember { mutableStateOf<ReunionResumen?>(null) }
    var reunionAEliminar by remember { mutableStateOf<ReunionResumen?>(null) }
    var mensajeNotificacion by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { AppTopBar(titulo = "Reuniones y capacitaciones", subtitulo = "Agenda distrital") },
        containerColor = FondoPantalla,
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = exito?.datos?.reuniones?.size?.let { "$it eventos programados" } ?: "", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                Button(
                    onClick = { mostrarDialogoNuevo = true },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                ) { Text("+ Agendar reunión") }
            }
            mensajeNotificacion?.let { texto ->
                Text(text = texto, color = VerdeOscuro, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            when (val estado = uiState) {
                UiState.Cargando -> EstadoCargando(modifier = Modifier.weight(1f))
                UiState.Vacio -> EstadoVacio(
                    mensaje = "No hay eventos programados. Usa '+ Agendar reunión' para crear el primero.",
                    modifier = Modifier.weight(1f),
                )
                is UiState.Error -> EstadoError(mensaje = estado.mensaje, modifier = Modifier.weight(1f))
                is UiState.Exito -> LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(estado.datos.reuniones) { reunion ->
                        ReunionCard(
                            reunion = reunion,
                            alTocar = { alAbrirAsistencia(reunion.id) },
                            alEditar = { reunionAEditar = reunion },
                            alEliminar = { reunionAEliminar = reunion },
                        )
                    }
                }
            }
        }

        if (mostrarDialogoNuevo) {
            ReunionFormDialog(
                reunion = null,
                alDesestimar = { mostrarDialogoNuevo = false },
                alGuardar = { nueva ->
                    viewModel.guardarReunion(nueva)
                    mostrarDialogoNuevo = false
                    mensajeNotificacion = "Evento '${nueva.tema}' agendado correctamente."
                },
            )
        }

        reunionAEditar?.let { r ->
            ReunionFormDialog(
                reunion = r,
                alDesestimar = { reunionAEditar = null },
                alGuardar = { editada ->
                    viewModel.guardarReunion(editada)
                    reunionAEditar = null
                    mensajeNotificacion = "Evento '${editada.tema}' actualizado."
                },
            )
        }

        reunionAEliminar?.let { r ->
            AlertDialog(
                onDismissRequest = { reunionAEliminar = null },
                title = { Text("¿Eliminar evento?") },
                text = { Text("¿Deseas eliminar '${r.tema}' de la agenda?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.eliminarReunion(
                                id = r.id,
                                alDesestimarConAsistencia = {
                                    mensajeNotificacion = "No se puede eliminar una reunión que ya tiene asistencia registrada."
                                },
                                alEliminarDefinitivo = {
                                    mensajeNotificacion = "Evento eliminado de la agenda."
                                },
                            )
                            reunionAEliminar = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RojoAlerta, contentColor = Color.White),
                    ) { Text("Eliminar") }
                },
                dismissButton = {
                    TextButton(onClick = { reunionAEliminar = null }) { Text("Cancelar") }
                },
            )
        }
    }
}

@Composable
private fun ReunionFormDialog(
    reunion: ReunionResumen?,
    alDesestimar: () -> Unit,
    alGuardar: (Reunion) -> Unit,
) {
    var tipo by remember { mutableStateOf(reunion?.tipo ?: TipoEvento.REUNION) }
    var tema by remember { mutableStateOf(reunion?.tema ?: "") }
    var fechaTexto by remember { mutableStateOf(reunion?.fecha?.toString() ?: "2026-09-15") }
    var horaInicioTexto by remember { mutableStateOf((reunion?.horaInicioMinutos?.div(60) ?: 9).toString()) }
    var horaFinTexto by remember { mutableStateOf((reunion?.horaFinMinutos?.div(60) ?: 11).toString()) }
    var lugar by remember { mutableStateOf(reunion?.lugar ?: "Local comunal Huata Centro") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = alDesestimar,
        title = { Text(if (reunion == null) "Agendar Evento" else "Editar Evento") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(text = "Tipo de evento", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = tipo == TipoEvento.REUNION,
                        onClick = { tipo = TipoEvento.REUNION },
                        label = { Text("Reunión") },
                    )
                    FilterChip(
                        selected = tipo == TipoEvento.CAPACITACION,
                        onClick = { tipo = TipoEvento.CAPACITACION },
                        label = { Text("Capacitación") },
                    )
                }

                OutlinedTextField(
                    value = tema,
                    onValueChange = { tema = it; error = null },
                    label = { Text("Tema / Asunto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = fechaTexto,
                    onValueChange = { fechaTexto = it; error = null },
                    label = { Text("Fecha (AAAA-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = horaInicioTexto,
                        onValueChange = { horaInicioTexto = it },
                        label = { Text("Hora inicio (h)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = horaFinTexto,
                        onValueChange = { horaFinTexto = it },
                        label = { Text("Hora fin (h)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }

                OutlinedTextField(
                    value = lugar,
                    onValueChange = { lugar = it },
                    label = { Text("Lugar / Sede") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                error?.let { err ->
                    Text(text = err, color = RojoAlerta, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tema.isBlank()) {
                        error = "Ingresa el tema del evento"
                        return@Button
                    }
                    val fechaParsed = try {
                        LocalDate.parse(fechaTexto.trim())
                    } catch (_: Exception) {
                        error = "Formato de fecha inválido (AAAA-MM-DD)"
                        return@Button
                    }
                    val hInicio = (horaInicioTexto.toIntOrNull() ?: 9) * 60
                    val hFin = (horaFinTexto.toIntOrNull() ?: 11) * 60
                    val idGenerado = reunion?.id ?: "R-0${(9..99).random()}"
                    val objeto = Reunion(
                        id = idGenerado,
                        tipo = tipo,
                        tema = tema.trim(),
                        fecha = fechaParsed,
                        horaInicioMinutos = hInicio,
                        horaFinMinutos = hFin,
                        lugar = lugar.trim(),
                    )
                    alGuardar(objeto)
                },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
            ) { Text("Guardar") }
        },
        dismissButton = {
            OutlinedButton(onClick = alDesestimar) { Text("Cancelar") }
        },
    )
}
