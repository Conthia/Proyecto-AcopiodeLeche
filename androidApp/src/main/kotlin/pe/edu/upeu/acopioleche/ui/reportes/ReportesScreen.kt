package pe.edu.upeu.acopioleche.ui.reportes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.presentation.core.UiState
import pe.edu.upeu.acopioleche.presentation.reportes.ReportesViewModel
import pe.edu.upeu.acopioleche.ui.components.AppCard
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.EstadoCargando
import pe.edu.upeu.acopioleche.ui.components.EstadoError
import pe.edu.upeu.acopioleche.ui.components.EstadoVacio
import pe.edu.upeu.acopioleche.ui.components.SectionLabel
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario

@Composable
fun ReportesScreen() {
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        ReportesViewModel(
            scope = scope,
            centroAcopioRepository = ServiceLocator.centroAcopioRepository,
            entregaRepository = ServiceLocator.entregaRepository,
        )
    }
    val uiState by viewModel.uiState.collectAsState()
    var mensajeNotificacion by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { AppTopBar(titulo = "Reportes", subtitulo = "Exportar e imprimir") },
        containerColor = FondoPantalla,
    ) { padding ->
        when (val estado = uiState) {
            UiState.Cargando -> EstadoCargando(modifier = Modifier.fillMaxSize().padding(padding))
            UiState.Vacio -> EstadoVacio(
                mensaje = "No hay reportes disponibles.",
                modifier = Modifier.fillMaxSize().padding(padding),
            )
            is UiState.Error -> EstadoError(mensaje = estado.mensaje, modifier = Modifier.fillMaxSize().padding(padding))
            is UiState.Exito -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(estado.datos.reportes) { reporte ->
                    ReporteRow(
                        reporte = reporte,
                        alGenerar = { mensajeNotificacion = "Generando ${reporte.formato} de \"${reporte.titulo}\"…" },
                    )
                }
                mensajeNotificacion?.let { mensaje ->
                    item {
                        androidx.compose.material3.Text(
                            text = mensaje,
                            color = TextoSecundario,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                item {
                    AppCard {
                        SectionLabel(texto = "Volumen por comunidad")
                        estado.datos.volumenPorComunidad.forEach { volumen -> VolumenComunidadRow(volumen = volumen) }
                    }
                }
            }
        }
    }
}
