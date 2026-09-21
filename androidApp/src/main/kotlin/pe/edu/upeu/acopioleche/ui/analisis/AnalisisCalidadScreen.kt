package pe.edu.upeu.acopioleche.ui.analisis

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.acopioleche.di.ServiceLocator
import pe.edu.upeu.acopioleche.presentation.analisis.AnalisisCalidadListaViewModel
import pe.edu.upeu.acopioleche.presentation.core.UiState
import pe.edu.upeu.acopioleche.ui.components.AppTopBar
import pe.edu.upeu.acopioleche.ui.components.AvatarIniciales
import pe.edu.upeu.acopioleche.ui.components.EstadoCargando
import pe.edu.upeu.acopioleche.ui.components.EstadoError
import pe.edu.upeu.acopioleche.ui.components.EstadoVacio
import pe.edu.upeu.acopioleche.ui.components.SectionLabel
import pe.edu.upeu.acopioleche.ui.theme.BordeTarjeta
import pe.edu.upeu.acopioleche.ui.theme.FondoPantalla
import pe.edu.upeu.acopioleche.ui.theme.FondoTarjeta
import pe.edu.upeu.acopioleche.ui.theme.TextoSecundario

@Composable
fun AnalisisCalidadScreen(
    alSeleccionar: (entregaId: String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        AnalisisCalidadListaViewModel(
            scope = scope,
            entregaRepository = ServiceLocator.entregaRepository,
            proveedorRepository = ServiceLocator.proveedorRepository,
            analisisCalidadRepository = ServiceLocator.analisisCalidadRepository,
        )
    }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { AppTopBar(titulo = "Análisis de calidad", subtitulo = "Entregas de hoy sin analizar") },
        containerColor = FondoPantalla,
    ) { padding ->
        when (val estado = uiState) {
            UiState.Cargando -> EstadoCargando(modifier = Modifier.fillMaxSize().padding(padding))
            UiState.Vacio -> EstadoVacio(
                mensaje = "No hay entregas pendientes de análisis.",
                modifier = Modifier.fillMaxSize().padding(padding),
            )
            is UiState.Error -> EstadoError(mensaje = estado.mensaje, modifier = Modifier.fillMaxSize().padding(padding))
            is UiState.Exito -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item { SectionLabel(texto = "Pendientes · ${estado.datos.pendientes.size}") }
                items(estado.datos.pendientes) { pendiente ->
                    Card(
                        onClick = { alSeleccionar(pendiente.entregaId) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
                        border = BorderStroke(width = 1.dp, color = BordeTarjeta),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            AvatarIniciales(nombre = pendiente.nombreProveedor)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = pendiente.nombreProveedor, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = "${pendiente.entregaId} · ${pendiente.volumenLitros} L",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextoSecundario,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
