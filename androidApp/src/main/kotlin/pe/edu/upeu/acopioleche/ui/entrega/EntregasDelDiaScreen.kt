package pe.edu.upeu.acopioleche.ui.entrega

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.di.SesionActivaHolder
import pe.edu.upeu.acopioleche.domain.model.Turno
import pe.edu.upeu.acopioleche.presentation.core.EntregaResumen
import pe.edu.upeu.acopioleche.presentation.entrega.EntregasDelDiaViewModel
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.EntregaRow
import pe.edu.upeu.acopioleche.ui.components.StatTile
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro
import kotlin.time.Clock

@Composable
fun EntregasDelDiaScreen(alVolver: () -> Unit) {
    val scope = rememberCoroutineScope()
    val sesion by SesionActivaHolder.sesion.collectAsState()
    val sesionActiva = requireNotNull(sesion) { "EntregasDelDiaScreen requiere una sesión activa" }
    val usuarioId = sesionActiva.usuarioId

    val viewModel = remember {
        EntregasDelDiaViewModel(
            scope = scope,
            entregaRepository = ServiceLocator.entregaRepository,
            proveedorRepository = ServiceLocator.proveedorRepository,
            analisisCalidadRepository = ServiceLocator.analisisCalidadRepository,
        )
    }
    val uiState by viewModel.uiState.collectAsState()
    val horaActual = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour }
    val turnoTexto = if (Turno.deducirDeHora(horaActual) == Turno.MANANA) "Turno mañana" else "Turno tarde"

    var entregaAEditar by remember { mutableStateOf<EntregaResumen?>(null) }
    var entregaACancelar by remember { mutableStateOf<EntregaResumen?>(null) }
    var mensajeNotificacion by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(titulo = "Entregas del día", subtitulo = "$turnoTexto · Huata Centro", alVolver = alVolver)
        },
        containerColor = FondoPantalla,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                AppCard {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatTile(
                            valor = "${uiState.totalLitros}",
                            etiqueta = "litros · turno",
                            modifier = Modifier.weight(1f),
                        )
                        StatTile(
                            valor = "${uiState.numeroProveedoresAtendidos}",
                            etiqueta = "proveedores atendidos",
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            mensajeNotificacion?.let { msg ->
                item { Text(text = msg, color = VerdeOscuro, style = MaterialTheme.typography.bodyMedium) }
            }
            items(uiState.entregas) { entrega ->
                EntregaRow(
                    entrega = entrega,
                    alEditar = { entregaAEditar = entrega },
                    alCancelar = { entregaACancelar = entrega },
                )
            }
        }

        entregaAEditar?.let { e ->
            var volumenTexto by remember { mutableStateOf(e.volumenLitros.toString()) }
            var porongosTexto by remember { mutableStateOf(e.cantidadPorongos.toString()) }
            var err by remember { mutableStateOf<String?>(null) }

            AlertDialog(
                onDismissRequest = { entregaAEditar = null },
                title = { Text("Editar entrega #${e.id}") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Proveedor: ${e.nombreProveedor}")
                        OutlinedTextField(
                            value = volumenTexto,
                            onValueChange = { volumenTexto = it; err = null },
                            label = { Text("Litros recogidos") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = porongosTexto,
                            onValueChange = { porongosTexto = it; err = null },
                            label = { Text("Cantidad porongos") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        err?.let { Text(text = it, color = RojoAlerta, style = MaterialTheme.typography.bodySmall) }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val vol = volumenTexto.toDoubleOrNull()
                            val por = porongosTexto.toIntOrNull()
                            if (vol == null || vol <= 0.0) {
                                err = "Ingresa un volumen válido"
                                return@Button
                            }
                            if (por == null || por < 1) {
                                err = "Ingresa cantidad de porongos válida"
                                return@Button
                            }
                            viewModel.editarEntrega(
                                id = e.id,
                                nuevoVolumen = vol,
                                nuevosPorongos = por,
                                alBloqueadoPorAnalisis = {
                                    mensajeNotificacion = "Acceso denegado: La entrega ya tiene análisis registrado o no está pendiente."
                                },
                                alExito = {
                                    mensajeNotificacion = "Entrega #${e.id} modificada correctamente."
                                },
                            )
                            entregaAEditar = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                    ) { Text("Guardar cambios") }
                },
                dismissButton = {
                    OutlinedButton(onClick = { entregaAEditar = null }) { Text("Cancelar") }
                },
            )
        }

        entregaACancelar?.let { e ->
            var motivoTexto by remember { mutableStateOf("Error de digitación al seleccionar proveedor") }

            AlertDialog(
                onDismissRequest = { entregaACancelar = null },
                title = { Text("¿Cancelar entrega #${e.id}?") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Esta acción anulará el registro. Ingrese el motivo de la cancelación:")
                        OutlinedTextField(
                            value = motivoTexto,
                            onValueChange = { motivoTexto = it },
                            label = { Text("Motivo de cancelación") },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.cancelarEntrega(
                                id = e.id,
                                motivo = motivoTexto.trim(),
                                usuarioId = usuarioId,
                                alBloqueadoPorAnalisis = {
                                    mensajeNotificacion = "Bloqueado por seguridad antifraude: La entrega ya fue analizada o procesada."
                                },
                                alExito = {
                                    mensajeNotificacion = "Entrega #${e.id} cancelada."
                                },
                            )
                            entregaACancelar = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RojoAlerta, contentColor = Color.White),
                    ) { Text("Confirmar cancelación") }
                },
                dismissButton = {
                    TextButton(onClick = { entregaACancelar = null }) { Text("Volver") }
                },
            )
        }
    }
}
