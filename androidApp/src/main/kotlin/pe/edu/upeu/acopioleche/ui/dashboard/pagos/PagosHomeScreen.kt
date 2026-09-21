package pe.edu.upeu.acopioleche.ui.dashboard.pagos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import kotlin.time.Clock
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.di.SesionActivaHolder
import pe.edu.upeu.acopioleche.domain.model.Liquidacion
import pe.edu.upeu.acopioleche.domain.model.PrecioTemporada
import pe.edu.upeu.acopioleche.presentation.dashboard.pagos.PagosHomeViewModel
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.EstadoBadge
import pe.edu.upeu.acopioleche.ui.components.SectionLabel
import pe.edu.upeu.acopioleche.ui.components.StatTile
import pe.edu.upeu.acopioleche.ui.theme.AmbarTexto
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro
import pe.edu.upeu.acopioleche.ui.theme.VerdeSuaveFondo

@Composable
fun PagosHomeScreen(
    alAbrirPerfil: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sesion by SesionActivaHolder.sesion.collectAsState()
    val sesionActiva = requireNotNull(sesion) { "PagosHomeScreen requiere una sesión activa" }

    val viewModel = remember(sesionActiva.usuarioId) {
        PagosHomeViewModel(
            scope = scope,
            liquidacionRepository = ServiceLocator.liquidacionRepository,
            pagoRepository = ServiceLocator.pagoRepository,
            precioTemporadaRepository = ServiceLocator.precioTemporadaRepository,
            sancionRepository = ServiceLocator.sancionRepository,
            reglasNegocio = ServiceLocator.reglasNegocio,
            proveedorRepository = ServiceLocator.proveedorRepository,
            entregaRepository = ServiceLocator.entregaRepository,
            encargadoId = sesionActiva.usuarioId,
            nombreEncargado = sesionActiva.nombreCompleto,
            centroAcopioId = sesionActiva.centroAcopioId,
        )
    }
    val uiState by viewModel.uiState.collectAsState()

    var liquidacionAPagar by remember { mutableStateOf<Liquidacion?>(null) }
    var mostrarDialogoPrecio by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Control de Pagos y Precios",
                subtitulo = "Encargado · ${uiState.nombreEncargado}",
                alAbrirPerfil = alAbrirPerfil,
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
                AppCard {
                    SectionLabel(texto = "Resumen de Liquidaciones de la Semana")
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatTile(valor = "S/ ${uiState.totalMontoPagadoSemana}", etiqueta = "total pagado", modifier = Modifier.weight(1f))
                        StatTile(valor = "S/ ${uiState.totalMontoPendienteSemana}", etiqueta = "total pendiente", color = RojoAlerta, modifier = Modifier.weight(1f))
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SectionLabel(texto = "Precios por Temporada (RF-28)")
                    Button(
                        onClick = { mostrarDialogoPrecio = true },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                    ) { Text("+ Nueva Tarifa") }
                }
            }

            items(uiState.preciosTemporada) { precio ->
                AppCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(text = precio.nombreTemporada, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "Vigencia: ${precio.fechaInicio} al ${precio.fechaFin}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario,
                            )
                        }
                        EstadoBadge(texto = "S/ ${precio.precioPorLitro} / L", color = VerdeOscuro, colorFondo = VerdeSuaveFondo)
                    }
                }
            }

            uiState.mensajeNotificacion?.let { msg ->
                item { Text(text = msg, color = VerdeOscuro, style = MaterialTheme.typography.bodyMedium) }
            }

            item { SectionLabel(texto = "Liquidaciones Semanales por Proveedor (RF-27)") }

            items(uiState.liquidacionesSemana) { liq ->
                val estaPagada = uiState.pagosRegistrados.any { it.liquidacionId == liq.id }
                val prov = uiState.proveedores.find { it.id == liq.proveedorId }
                AppCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(text = prov?.nombre ?: liq.proveedorId, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "Semana del ${liq.semanaInicio} · ${liq.litrosAceptados} L aceptados",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario,
                            )
                        }
                        Text(text = "S/ ${liq.montoFinal}", style = MaterialTheme.typography.titleLarge, color = VerdeOscuro)
                    }
                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (estaPagada) {
                            Text(text = "PAGADO (RN-21)", color = VerdeOscuro, style = MaterialTheme.typography.labelLarge)
                        } else {
                            Text(text = "PENDIENTE DE PAGO", color = AmbarTexto, style = MaterialTheme.typography.labelMedium)
                            OutlinedButton(onClick = { liquidacionAPagar = liq }) {
                                Text("Registrar Pago")
                            }
                        }
                    }
                }
            }
        }

        liquidacionAPagar?.let { liq ->
            var metodo by remember { mutableStateOf("EFECTIVO") }
            var notas by remember { mutableStateOf("Entrega presencial") }

            AlertDialog(
                onDismissRequest = { liquidacionAPagar = null },
                title = { Text("Registrar Pago Manual (RN-21)") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Liquidación #${liq.id} · Monto: S/ ${liq.montoFinal}")
                        Text("Constancia de entrega presencial con fecha y hora.")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = metodo == "EFECTIVO",
                                onClick = { metodo = "EFECTIVO" },
                                label = { Text("Efectivo") },
                            )
                            FilterChip(
                                selected = metodo == "DEPOSITO",
                                onClick = { metodo = "DEPOSITO" },
                                label = { Text("Depósito") },
                            )
                        }
                        OutlinedTextField(
                            value = notas,
                            onValueChange = { notas = it },
                            label = { Text("Notas / Constancia") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.registrarPagoManual(
                                liquidacion = liq,
                                metodoPago = metodo,
                                notas = notas.trim(),
                            )
                            liquidacionAPagar = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                    ) { Text("Confirmar Pago") }
                },
                dismissButton = {
                    TextButton(onClick = { liquidacionAPagar = null }) { Text("Cancelar") }
                },
            )
        }

        if (mostrarDialogoPrecio) {
            var nombre by remember { mutableStateOf("Temporada Intermedia") }
            var fInicioTexto by remember { mutableStateOf("2026-06-01") }
            var fFinTexto by remember { mutableStateOf("2026-08-31") }
            var precioTexto by remember { mutableStateOf("1.75") }
            var err by remember { mutableStateOf<String?>(null) }

            AlertDialog(
                onDismissRequest = { mostrarDialogoPrecio = false },
                title = { Text("Nueva Tarifa por Temporada (RF-28)") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre de temporada") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = fInicioTexto,
                            onValueChange = { fInicioTexto = it },
                            label = { Text("Fecha Inicio (AAAA-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = fFinTexto,
                            onValueChange = { fFinTexto = it },
                            label = { Text("Fecha Fin (AAAA-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = precioTexto,
                            onValueChange = { precioTexto = it },
                            label = { Text("Precio por Litro (S/)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        err?.let { Text(text = it, color = RojoAlerta, style = MaterialTheme.typography.bodySmall) }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val precio = precioTexto.toDoubleOrNull()
                            val fIn = try { LocalDate.parse(fInicioTexto.trim()) } catch (_: Exception) { null }
                            val fFi = try { LocalDate.parse(fFinTexto.trim()) } catch (_: Exception) { null }

                            if (precio == null || precio <= 0.0 || fIn == null || fFi == null) {
                                err = "Ingresa datos y fechas válidas"
                                return@Button
                            }

                            val objeto = PrecioTemporada(
                                id = "PT-${Clock.System.now().toEpochMilliseconds()}",
                                nombreTemporada = nombre.trim(),
                                fechaInicio = fIn,
                                fechaFin = fFi,
                                precioPorLitro = precio,
                            )
                            viewModel.guardarPrecioTemporada(objeto)
                            mostrarDialogoPrecio = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                    ) { Text("Guardar Tarifa") }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoPrecio = false }) { Text("Cancelar") }
                },
            )
        }
    }
}
