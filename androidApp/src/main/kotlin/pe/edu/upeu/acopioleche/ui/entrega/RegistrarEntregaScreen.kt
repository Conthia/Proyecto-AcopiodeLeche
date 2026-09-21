package pe.edu.upeu.acopioleche.ui.entrega

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.di.SesionActivaHolder
import pe.edu.upeu.acopioleche.domain.model.Turno
import pe.edu.upeu.acopioleche.presentation.entrega.RegistrarEntregaViewModel
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.SectionLabel
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

@Composable
fun RegistrarEntregaScreen(
    proveedorId: String,
    alVolver: () -> Unit,
    alTerminar: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sesion by SesionActivaHolder.sesion.collectAsState()
    val sesionActiva = requireNotNull(sesion) { "RegistrarEntregaScreen requiere una sesión activa" }
    val viewModel = remember(sesionActiva.usuarioId, proveedorId) {
        RegistrarEntregaViewModel(
            scope = scope,
            entregaRepository = ServiceLocator.entregaRepository,
            proveedorRepository = ServiceLocator.proveedorRepository,
            notificacionRepository = ServiceLocator.notificacionRepository,
            rutaRepository = ServiceLocator.rutaRepository,
            centroAcopioId = requireNotNull(sesionActiva.centroAcopioId) { "El acopiador debe tener un centro de acopio asignado" },
            acopiadorId = sesionActiva.usuarioId,
            proveedorId = proveedorId,
        )
    }
    val uiState by viewModel.uiState.collectAsState()
    val entregaGuardada = uiState.entregaGuardada

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = if (entregaGuardada != null) "Comprobante" else "Nueva entrega",
                subtitulo = if (entregaGuardada != null) entregaGuardada.id else "Paso 2 de 2",
                alVolver = alVolver,
            )
        },
        containerColor = FondoPantalla,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (entregaGuardada != null) {
                ConfirmacionEntregaContent(
                    entrega = entregaGuardada,
                    alRegistrarOtra = alVolver,
                    alVolverAlInicio = alTerminar,
                )
            } else {
                AppCard {
                    SectionLabel(texto = "Proveedor")
                    Text(text = uiState.proveedor?.nombre ?: "Selecciona un proveedor", style = MaterialTheme.typography.bodyLarge)
                }

                if (!uiState.esNoRecogida) {
                    AppCard {
                        SectionLabel(texto = "Volumen recibido")
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = if (uiState.volumenLitrosTexto.isEmpty()) "0" else uiState.volumenLitrosTexto,
                                style = MaterialTheme.typography.headlineLarge,
                            )
                            Text(text = " L", style = MaterialTheme.typography.bodyLarge, color = TextoSecundario)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(5, 10, 20).forEach { cantidad ->
                                OutlinedButton(onClick = { viewModel.onAgregarLitros(cantidad) }, modifier = Modifier.weight(1f)) {
                                    Text("+$cantidad")
                                }
                            }
                        }
                        OutlinedTextField(
                            value = uiState.volumenLitrosTexto,
                            onValueChange = { viewModel.onVolumenLitrosChange(it) },
                            label = { Text("Litros exactos") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        )
                    }

                    AppCard {
                        SectionLabel(texto = "Turno (automático)")
                        Text(
                            text = if (uiState.turno == Turno.MANANA) "Turno Mañana (antes de 12:00)" else "Turno Tarde (desde 12:00)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario,
                        )

                        SectionLabel(texto = "Porongos entregados")
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(onClick = { viewModel.onPorongosMenos() }, modifier = Modifier.width(52.dp).height(48.dp)) {
                                Text("−")
                            }
                            Text(
                                text = "${uiState.cantidadPorongos}",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                            )
                            OutlinedButton(onClick = { viewModel.onPorongosMas() }, modifier = Modifier.width(52.dp).height(48.dp)) {
                                Text("+")
                            }
                        }
                    }
                } else {
                    AppCard {
                        SectionLabel(texto = "Motivo de no recojo")
                        listOf("No había leche / Sin producción", "Proveedor ausente", "Camino inaccesible").forEach { motivo ->
                            FilterChip(
                                selected = uiState.motivoNoRecogida == motivo,
                                onClick = { viewModel.onMotivoNoRecogidaChange(motivo) },
                                label = { Text(motivo) },
                                modifier = Modifier.padding(vertical = 2.dp),
                            )
                        }
                    }
                }

                AppCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Marcar como No Recogido (Sin acopio)")
                        Switch(
                            checked = uiState.esNoRecogida,
                            onCheckedChange = { viewModel.onMarcarNoRecogida(it) },
                        )
                    }
                }

                uiState.mensajeError?.let { error ->
                    Text(text = error, color = RojoAlerta, style = MaterialTheme.typography.bodyMedium)
                }

                Button(
                    onClick = { viewModel.onGuardarClick() },
                    enabled = !uiState.guardando,
                    modifier = Modifier.fillMaxWidth().height(66.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                ) {
                    Text(if (uiState.guardando) "Guardando…" else "Guardar entrega", style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    text = "Se guarda en el equipo · no necesita internet",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextoSecundario,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}
