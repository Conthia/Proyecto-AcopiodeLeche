package pe.edu.upeu.acopioleche.ui.dashboard.productor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.di.SesionActivaHolder
import pe.edu.upeu.acopioleche.domain.model.ResultadoAnalisis
import pe.edu.upeu.acopioleche.presentation.dashboard.productor.ProductorHomeViewModel
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.EntregaRow
import pe.edu.upeu.acopioleche.ui.components.EstadoBadge
import pe.edu.upeu.acopioleche.ui.components.SectionLabel
import pe.edu.upeu.acopioleche.ui.components.StatTile
import pe.edu.upeu.acopioleche.ui.theme.AmbarFondo
import pe.edu.upeu.acopioleche.ui.theme.AmbarTexto
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.RojoAlerta
import pe.edu.upeu.acopioleche.ui.theme.RojoFondo
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario
import pe.edu.upeu.acopioleche.ui.theme.VerdeOscuro
import pe.edu.upeu.acopioleche.ui.theme.VerdeSuaveFondo

@Composable
fun ProductorHomeScreen(
    alAbrirPerfil: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sesion by SesionActivaHolder.sesion.collectAsState()
    val sesionActiva = requireNotNull(sesion) { "ProductorHomeScreen requiere una sesión activa" }
    val proveedorId = requireNotNull(sesionActiva.proveedorId) { "Un productor debe estar asociado a un proveedorId" }

    val viewModel = remember(proveedorId) {
        ProductorHomeViewModel(
            scope = scope,
            entregaRepository = ServiceLocator.entregaRepository,
            proveedorRepository = ServiceLocator.proveedorRepository,
            analisisCalidadRepository = ServiceLocator.analisisCalidadRepository,
            liquidacionRepository = ServiceLocator.liquidacionRepository,
            proveedorId = proveedorId,
            nombreProductor = sesionActiva.nombreCompleto,
        )
    }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Mi Panel de Productor",
                subtitulo = uiState.nombreProductor,
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
                    SectionLabel(texto = "Resumen de acopio de leche (RF-37)")
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatTile(valor = "${uiState.litrosHoy} L", etiqueta = "leche entregada hoy", modifier = Modifier.weight(1f))
                        StatTile(valor = "${uiState.litrosAcumuladoSemana} L", etiqueta = "acumulado semanal", modifier = Modifier.weight(1f))
                    }
                }
            }

            item { SectionLabel(texto = "Mis Entregas Registradas (${uiState.misEntregas.size})") }

            items(uiState.misEntregas) { entrega ->
                EntregaRow(entrega = entrega, mostrarAvatar = false)
            }

            if (uiState.misAnalisis.isNotEmpty()) {
                item { SectionLabel(texto = "Mis Resultados de Calidad de Leche (${uiState.misAnalisis.size})") }
                items(uiState.misAnalisis) { analisis ->
                    AppCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = "Análisis #${analisis.id}", style = MaterialTheme.typography.titleMedium)
                            when (analisis.resultado) {
                                is ResultadoAnalisis.Normal -> EstadoBadge(texto = "Calidad Normal", color = VerdeOscuro, colorFondo = VerdeSuaveFondo)
                                is ResultadoAnalisis.FueraDeRango -> EstadoBadge(texto = "Fuera de Rango", color = RojoAlerta, colorFondo = RojoFondo)
                                is ResultadoAnalisis.Adulterada -> EstadoBadge(texto = "Adulterada", color = AmbarTexto, colorFondo = AmbarFondo)
                            }
                        }
                        Text(text = "Fecha: ${analisis.fecha}", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                    }
                }
            }

            if (uiState.misLiquidaciones.isNotEmpty()) {
                item { SectionLabel(texto = "Mis Pagos y Liquidaciones (RF-35)") }
                items(uiState.misLiquidaciones) { liq ->
                    AppCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(text = "Semana del ${liq.semanaInicio}", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = "${liq.litrosAceptados} L aceptados · Pago: ${liq.fechaPago}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextoSecundario,
                                )
                            }
                            Text(text = "S/ ${liq.montoFinal}", style = MaterialTheme.typography.titleLarge, color = VerdeOscuro)
                        }
                        HorizontalDivider()
                        Text(
                            text = "Liquidación Ref: ${liq.id}",
                            style = MaterialTheme.typography.labelMedium,
                            color = VerdeOscuro,
                        )
                    }
                }
            }
        }
    }
}
