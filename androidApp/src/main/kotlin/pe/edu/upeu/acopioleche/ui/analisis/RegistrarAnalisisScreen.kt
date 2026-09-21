package pe.edu.upeu.acopioleche.ui.analisis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.di.SesionActivaHolder
import pe.edu.upeu.acopioleche.domain.model.CriterioAnalisis
import pe.edu.upeu.acopioleche.domain.model.OrigenDatoAnalisis
import pe.edu.upeu.acopioleche.domain.model.ResultadoAnalisis
import pe.edu.upeu.acopioleche.domain.model.ResultadoSancion
import pe.edu.upeu.acopioleche.presentation.analisis.RegistrarAnalisisViewModel
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.SectionLabel
import pe.edu.upeu.acopioleche.ui.theme.AmbarPendiente
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro

@Composable
fun RegistrarAnalisisScreen(
    entregaId: String,
    alVolver: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sesion by SesionActivaHolder.sesion.collectAsState()
    val sesionActiva = requireNotNull(sesion) { "RegistrarAnalisisScreen requiere una sesión activa" }
    val viewModel = remember(entregaId) {
        RegistrarAnalisisViewModel(
            scope = scope,
            entregaRepository = ServiceLocator.entregaRepository,
            proveedorRepository = ServiceLocator.proveedorRepository,
            analisisCalidadRepository = ServiceLocator.analisisCalidadRepository,
            sancionRepository = ServiceLocator.sancionRepository,
            capacitacionCorrectivaRepository = ServiceLocator.capacitacionCorrectivaRepository,
            notificacionRepository = ServiceLocator.notificacionRepository,
            reglasNegocio = ServiceLocator.reglasNegocio,
            entregaId = entregaId,
            tecnicoId = sesionActiva.usuarioId,
        )
    }
    val uiState by viewModel.uiState.collectAsState()
    val resultado = uiState.resultadoGuardado

    Scaffold(
        topBar = { AppTopBar(titulo = "Análisis en campo (LactoScan)", subtitulo = uiState.nombreProveedor, alVolver = alVolver) },
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
            if (resultado != null) {
                ResultadoAnalisisContent(
                    resultado = resultado,
                    sancion = uiState.sancionAplicada,
                    advertenciaEstado = uiState.advertenciaEstado,
                    alVolver = alVolver,
                )
            } else {
                AppCard {
                    SectionLabel(texto = "Criterio de selección para análisis (RN-24)")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        CriterioAnalisis.entries.forEach { criterio ->
                            val etiqueta = when (criterio) {
                                CriterioAnalisis.PROGRAMADO -> "Programado"
                                CriterioAnalisis.ALEATORIO -> "Aleatorio"
                                CriterioAnalisis.HISTORIAL_ALERTA -> "Alerta previo"
                                CriterioAnalisis.MANUAL -> "Manual"
                            }
                            FilterChip(
                                selected = uiState.criterioSeleccion == criterio,
                                onClick = { viewModel.onCriterioSeleccionChange(criterio) },
                                label = { Text(etiqueta) },
                            )
                        }
                    }
                }

                AppCard {
                    SectionLabel(texto = "Captura de parámetros (RF-29 / RN-25)")
                    OutlinedButton(
                        onClick = { viewModel.simularEscaneoOCRTicket() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                    ) {
                        Text("📷 Simular escaneo de ticket (DEMO)")
                    }
                    Text(
                        text = if (uiState.origenDato == OrigenDatoAnalisis.OCR_COMPROBANTE)
                            "Origen: Escaneo OCR detectado"
                        else
                            "Origen: Ingreso Manual (siempre disponible abajo)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario,
                    )
                    HorizontalDivider()

                    CampoNumerico(etiqueta = "Densidad", valor = uiState.densidadTexto, onValueChange = viewModel::onDensidadChange)
                    CampoNumerico(etiqueta = "Grasa (%)", valor = uiState.grasaTexto, onValueChange = viewModel::onGrasaChange)
                    CampoNumerico(etiqueta = "Proteína (%)", valor = uiState.proteinaTexto, onValueChange = viewModel::onProteinaChange)
                    CampoNumerico(etiqueta = "Lactosa (%)", valor = uiState.lactosaTexto, onValueChange = viewModel::onLactosaChange)
                    CampoNumerico(etiqueta = "Temperatura (°C)", valor = uiState.temperaturaTexto, onValueChange = viewModel::onTemperaturaChange)
                    CampoNumerico(etiqueta = "pH", valor = uiState.phTexto, onValueChange = viewModel::onPhChange)
                    CampoNumerico(
                        etiqueta = "Agua añadida (%)",
                        valor = uiState.porcentajeAguaTexto,
                        onValueChange = viewModel::onPorcentajeAguaChange,
                    )
                }

                AppCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("Comprobante firmado por el productor (RN-26)")
                            Text("Verificación de conformidad en campo", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                        }
                        Switch(
                            checked = uiState.firmaProductorPresente,
                            onCheckedChange = { viewModel.onFirmaProductorChange(it) },
                        )
                    }
                }

                uiState.mensajeError?.let { error ->
                    Text(text = error, color = RojoAlerta, style = MaterialTheme.typography.bodyMedium)
                }

                Button(
                    onClick = { viewModel.onGuardarClick() },
                    enabled = !uiState.guardando,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
                ) {
                    Text(if (uiState.guardando) "Evaluando…" else "Guardar y evaluar análisis")
                }
            }
        }
    }
}

@Composable
private fun CampoNumerico(etiqueta: String, valor: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(etiqueta) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun ResultadoAnalisisContent(
    resultado: ResultadoAnalisis,
    sancion: ResultadoSancion?,
    advertenciaEstado: String?,
    alVolver: () -> Unit,
) {
    val (color, titulo, detalle) = when (resultado) {
        is ResultadoAnalisis.Normal -> Triple(
            VerdeOscuro,
            "Calidad normal",
            "grasa ${resultado.grasa}% · proteína ${resultado.proteina}% · lactosa ${resultado.lactosa}% · temp ${resultado.temperatura}°C · pH ${resultado.ph}",
        )
        is ResultadoAnalisis.FueraDeRango -> Triple(
            RojoAlerta,
            "Entrega rechazada automáticamente",
            "${resultado.motivo} · valor medido ${resultado.valorMedido}",
        )
        is ResultadoAnalisis.Adulterada -> Triple(AmbarPendiente, "Adulteración detectada", resultado.indicio)
    }

    AppCard {
        SectionLabel(texto = "Resultado")
        Text(text = titulo, style = MaterialTheme.typography.titleMedium, color = color)
        Text(text = detalle, style = MaterialTheme.typography.bodyMedium)
    }

    sancion?.let { SancionAplicadaCard(sancion = it) }

    advertenciaEstado?.let { advertencia ->
        AppCard {
            SectionLabel(texto = "Requiere revisión manual (RN-20)")
            Text(text = advertencia, style = MaterialTheme.typography.bodyMedium, color = RojoAlerta)
        }
    }

    Button(
        onClick = alVolver,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro, contentColor = Color.White),
    ) {
        Text("Volver")
    }
}

@Composable
private fun SancionAplicadaCard(sancion: ResultadoSancion) {
    val (color, mensaje) = when (sancion) {
        is ResultadoSancion.ReducirPrecioSemanal ->
            AmbarPendiente to "Primera adulteración: la entrega se acepta, pero se reduce el precio de toda la semana del proveedor."
        is ResultadoSancion.RetirarYMultar ->
            RojoAlerta to "Segunda adulteración detectada: proveedor retirado del padrón + multa de S/ ${sancion.montoMulta}."
        is ResultadoSancion.RetirarInmediato ->
            RojoAlerta to "Adulteración ≥ 5%: proveedor retirado del padrón de inmediato, sin sanción progresiva."
    }
    AppCard {
        SectionLabel(texto = "Sanción aplicada (RF-19)")
        Text(text = mensaje, style = MaterialTheme.typography.bodyMedium, color = color)
    }
}
