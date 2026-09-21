package pe.edu.upeu.acopioleche.ui.dashboard.acopiador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.di.SesionActivaHolder
import pe.edu.upeu.acopioleche.domain.model.EstadoRuta
import pe.edu.upeu.acopioleche.domain.model.Turno
import pe.edu.upeu.acopioleche.presentation.dashboard.acopiador.AcopiadorHomeViewModel
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.EntregaRow
import pe.edu.upeu.acopioleche.ui.components.EstadoBadge
import pe.edu.upeu.acopioleche.ui.components.SectionLabel
import pe.edu.upeu.acopioleche.ui.components.StatTile
import pe.edu.upeu.acopioleche.ui.components.TopBarChip
import pe.edu.upeu.acopioleche.ui.proveedor.ProveedorRow
import pe.edu.upeu.acopioleche.ui.theme.AmbarFondo
import pe.edu.upeu.acopioleche.ui.theme.AmbarPendiente
import pe.edu.upeu.acopioleche.ui.theme.AmbarTexto
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro
import pe.edu.upeu.acopioleche.ui.theme.VerdeSuaveFondo
import kotlin.time.Clock

@Composable
fun AcopiadorHomeScreen(
    alRegistrarEntrega: () -> Unit,
    alVerEntregasDelDia: () -> Unit,
    alVerColaDeEnvio: () -> Unit,
    alAbrirPerfil: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sesion by SesionActivaHolder.sesion.collectAsState()
    val sesionActiva = requireNotNull(sesion) { "AcopiadorHomeScreen requiere una sesión activa" }
    val viewModel = remember(sesionActiva.usuarioId) {
        AcopiadorHomeViewModel(
            scope = scope,
            entregaRepository = ServiceLocator.entregaRepository,
            proveedorRepository = ServiceLocator.proveedorRepository,
            rutaRepository = ServiceLocator.rutaRepository,
            acopiadorId = sesionActiva.usuarioId,
            nombreAcopiador = sesionActiva.nombreCompleto,
        )
    }
    val uiState by viewModel.uiState.collectAsState()

    var mostrarDialogoCierre by remember { mutableStateOf(false) }
    var mensajeNotificacion by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Acopio de hoy",
                subtitulo = "Acopiador · ${uiState.nombreAcopiador}",
                alAbrirPerfil = alAbrirPerfil,
                accesorio = { TopBarChip(texto = "${uiState.numeroPendientes} pendientes de envío") },
            )
        },
        containerColor = FondoPantalla,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                val horaActual = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour }
                val turnoTexto = if (Turno.deducirDeHora(horaActual) == Turno.MANANA) "Mañana" else "Tarde"
                AppCard {
                    SectionLabel(texto = "Progreso del turno actual · $turnoTexto")
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatTile(valor = "${uiState.litrosHoy}", etiqueta = "litros hoy", modifier = Modifier.weight(1f))
                        StatTile(valor = "${uiState.numeroEntregas}", etiqueta = "entregas", modifier = Modifier.weight(1f))
                        StatTile(
                            valor = "${uiState.numeroPendientes}",
                            etiqueta = "sin enviar",
                            color = if (uiState.numeroPendientes > 0) AmbarPendiente else VerdeOscuro,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            uiState.rutaActual?.let { ruta ->
                item {
                    AppCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                SectionLabel(texto = "Ruta Asignada del Día")
                                Text(text = ruta.nombre, style = MaterialTheme.typography.titleMedium)
                            }
                            if (ruta.estado == EstadoRuta.EN_CURSO) {
                                EstadoBadge(texto = "En Curso", color = VerdeOscuro, colorFondo = VerdeSuaveFondo)
                            } else {
                                EstadoBadge(texto = "Finalizada", color = AmbarTexto, colorFondo = AmbarFondo)
                            }
                        }
                        Text(
                            text = "${ruta.paradas.size} paradas ordenadas · Centro de sector: ${ruta.centroSectorId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario,
                        )
                        HorizontalDivider()

                        if (ruta.estado == EstadoRuta.EN_CURSO) {
                            Button(
                                onClick = { mostrarDialogoCierre = true },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                            ) {
                                Text("Cierre de Ruta y Descarga Consolidada")
                            }
                        }
                    }
                }
            }

            mensajeNotificacion?.let { msg ->
                item { Text(text = msg, color = VerdeOscuro, style = MaterialTheme.typography.bodyMedium) }
            }

            item {
                Button(
                    onClick = alRegistrarEntrega,
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                ) {
                    Text(text = "+  Registrar entrega nueva", style = MaterialTheme.typography.titleMedium)
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    // Sin altura fija: en pantallas angostas estas etiquetas envuelven a 2 líneas
                    // y una altura fija las recortaría.
                    OutlinedButton(
                        onClick = alVerEntregasDelDia,
                        modifier = Modifier.weight(1f).heightIn(min = 50.dp),
                    ) {
                        Text("Entregas del día", textAlign = TextAlign.Center)
                    }
                    OutlinedButton(
                        onClick = alVerColaDeEnvio,
                        modifier = Modifier.weight(1f).heightIn(min = 50.dp),
                    ) {
                        Text("Pendientes de envío", textAlign = TextAlign.Center)
                    }
                }
            }

            if (uiState.proveedoresPendientesHoy.isNotEmpty()) {
                item {
                    SectionLabel(texto = "Productores asignados pendientes hoy (${uiState.proveedoresPendientesHoy.size})")
                }
                items(uiState.proveedoresPendientesHoy) { proveedor ->
                    ProveedorRow(proveedor = proveedor)
                }
            }

            item { SectionLabel(texto = "Últimas entregas de hoy") }

            items(uiState.ultimasEntregas) { entrega ->
                EntregaRow(entrega = entrega)
            }
        }

        if (mostrarDialogoCierre) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoCierre = false },
                title = { Text("¿Realizar Cierre de Ruta?") },
                text = {
                    Text("Se registrará UNA SOLA descarga con el volumen acumulado total de ${uiState.litrosHoy} L en el centro de sector. Esta acción finalizará el recorrido del acopiador para esta jornada.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.realizarCierreDeRuta { volumenTotal ->
                                mensajeNotificacion = "Cierre de Ruta exitoso. Descarga de $volumenTotal L registrada en el centro de sector."
                                mostrarDialogoCierre = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                    ) { Text("Confirmar Cierre de Ruta") }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoCierre = false }) { Text("Cancelar") }
                },
            )
        }
    }
}
